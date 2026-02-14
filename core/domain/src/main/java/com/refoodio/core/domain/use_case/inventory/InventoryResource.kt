package com.refoodio.core.domain.use_case.inventory

import com.refoodio.core.domain.util.AppError
import com.refoodio.core.domain.util.Resource

typealias InventoryResource<T> = Resource<T, AppError> // Hem Validate hem CommonError alabilmesi için