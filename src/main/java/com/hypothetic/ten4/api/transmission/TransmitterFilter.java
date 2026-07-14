package com.hypothetic.ten4.api.transmission;

import org.jetbrains.annotations.Nullable;

public interface TransmitterFilter<AC> {
  AC getFiltered(@Nullable AC resource);
}
