# Converts old recipe JSON format (form+type+key) to new format (item/item_tag/fluid/fluid_tag)
# Usage: .\migrate_recipes.ps1 [-DryRun]

param([switch]$DryRun)

$ErrorActionPreference = "Stop"
$recipeDir = "src\main\resources\data\ten4\recipes"
$customTypes = @("ten4:pulverizer", "ten4:compressor", "ten4:psionicant", "ten4:induction_furnace", "ten4:refiner")

$changed = 0
$skipped = 0

function New-Ingredient($old) {
    # Already in new format? Return as-is (don't modify)
    if (-not $old.PSObject.Properties['key']) {
        # Return a clone to signal "keep this as-is"
        $clone = @{}
        foreach ($prop in $old.PSObject.Properties) {
            $clone[$prop.Name] = $prop.Value
        }
        return $clone
    }

    $new = @{}
    $form = if ($old.form) { $old.form } else { "item" }
    $type = if ($old.type) { $old.type } else { "static" }

    if ($form -eq "item" -and $type -eq "static")  { $new["item"] = $old.key }
    elseif ($form -eq "item" -and $type -eq "tag") { $new["item_tag"] = $old.key }
    elseif ($form -eq "fluid" -and $type -eq "static") { $new["fluid"] = $old.key }
    elseif ($form -eq "fluid" -and $type -eq "tag") { $new["fluid_tag"] = $old.key }
    else { return $null }

    # count / amount
    if ($form -eq "item") {
        if ($old.PSObject.Properties['count'] -and $old.count -ne 1) {
            $new["count"] = [int]$old.count
        }
    }
    else {
        if ($old.PSObject.Properties['amount']) {
            $new["amount"] = [int]$old.amount
        }
    }

    # chance — skip default 1.0
    if ($old.PSObject.Properties['chance']) {
        $c = [double]$old.chance
        if ($c -ne 1.0) { $new["chance"] = $c }
    }

    return $new
}

function Format-JsonValue($val) {
    if ($null -eq $val) { return "null" }
    if ($val -is [string]) { return '"' + $val + '"' }
    if ($val -is [bool]) { return $val.ToString().ToLower() }
    if ($val -is [double]) {
        if ($val -eq [math]::Floor($val)) { return $val.ToString("0") }
        return $val.ToString()
    }
    if ($val -is [int] -or $val -is [long]) { return $val.ToString() }
    return [string]$val
}

function Format-Json($obj, $indentLevel) {
    $p = "  " * $indentLevel
    $pc = "  " * ($indentLevel + 1)

    if ($obj -is [array]) {
        if ($obj.Count -eq 0) { return "[]" }
        $lines = @()
        $lines += "["
        for ($i = 0; $i -lt $obj.Count; $i++) {
            $comma = if ($i -lt $obj.Count - 1) { "," } else { "" }
            $lines += ($pc + (Format-Json $obj[$i] ($indentLevel + 1)) + $comma)
        }
        $lines += ($p + "]")
        return ($lines -join "`n")
    }

    if ($obj -is [System.Collections.IDictionary] -or $obj -is [PSCustomObject]) {
        $keys = @(if ($obj -is [PSCustomObject]) { ($obj | Get-Member -MemberType NoteProperty).Name } else { $obj.Keys })
        if ($keys.Count -eq 0) { return "{}" }
        $lines = @()
        $lines += "{"
        for ($i = 0; $i -lt $keys.Count; $i++) {
            $k = $keys[$i]
            $v = if ($obj -is [PSCustomObject]) { $obj.$k } else { $obj[$k] }
            $comma = if ($i -lt $keys.Count - 1) { "," } else { "" }
            $lines += ($pc + '"' + $k + '": ' + (Format-Json $v ($indentLevel + 1)) + $comma)
        }
        $lines += ($p + "}")
        return ($lines -join "`n")
    }

    return Format-JsonValue $obj
}

Get-ChildItem -Path $recipeDir -Recurse -Filter "*.json" | ForEach-Object {
    $file = $_.FullName
    $relPath = $file.Replace((Get-Location).Path + '\', '')

    $json = Get-Content $file -Raw -Encoding UTF8 | ConvertFrom-Json

    # Only process custom mod recipes
    if ($json.type -notin $customTypes) {
        $skipped++
        return
    }

    $dirty = $false

    # Fix already-converted files with broken single-element array unwrapping
    # (PowerShell ConvertFrom-Json unwraps single-element arrays)
    if ($json.inputs -and -not ($json.inputs -is [array])) {
        $json.inputs = @($json.inputs)
        $dirty = $true
    }
    if ($json.outputs -and -not ($json.outputs -is [array])) {
        $json.outputs = @($json.outputs)
        $dirty = $true
    }

    # Transform inputs
    if ($json.inputs) {
        $ni = @()
        foreach ($ing in $json.inputs) {
            $newIng = New-Ingredient $ing
            if ($newIng) { $dirty = $true; $ni += $newIng }
        }
        if ($ni.Count -gt 0) { $json.inputs = $ni }
    }

    # Transform outputs
    if ($json.outputs) {
        $no = @()
        foreach ($ing in $json.outputs) {
            $newIng = New-Ingredient $ing
            if ($newIng) { $dirty = $true; $no += $newIng }
        }
        if ($no.Count -gt 0) { $json.outputs = $no }
    }

    if ($dirty) {
        # Build a dictionary with proper key order: type, inputs, outputs, time
        $ordered = [ordered]@{}
        $ordered["type"] = $json.type

        # Force array types — PS 5.1 unwraps single-element arrays from PSCustomObject props
        $ordered["inputs"] = [object[]]$ni
        $ordered["outputs"] = [object[]]$no

        if ($json.PSObject.Properties['time']) {
            $ordered["time"] = [int]$json.time
        }

        # Use built-in ConvertTo-Json (handles arrays correctly unlike custom formatter)
        $out = $ordered | ConvertTo-Json -Depth 10
        # Fix: ConvertTo-Json escapes angle brackets unnecessarily
        $out = $out -replace '\\u003c', '<' -replace '\\u003e', '>'
        # Ensure trailing newline
        if (-not $out.EndsWith("`n")) { $out += "`n" }

        if (-not $DryRun) {
            $utf8 = New-Object System.Text.UTF8Encoding $false
            [System.IO.File]::WriteAllText($file, $out, $utf8)
        }
        $changed++
        Write-Host "CONVERTED: $relPath"
    }
}

Write-Host ""
Write-Host "Done. Changed: $changed, Skipped: $skipped"
if ($DryRun) { Write-Host "DRY RUN - no files were modified." }
