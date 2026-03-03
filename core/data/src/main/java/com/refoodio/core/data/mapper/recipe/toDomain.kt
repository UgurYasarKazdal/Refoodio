package com.refoodio.core.data.mapper.recipe

import com.refoodio.core.data.remote.recipe.RecipeDto
import com.refoodio.core.domain.model.recipe.Recipe

// com.refoodio.data.mapper.RecipeMapper
fun RecipeDto.toDomain(): Recipe {
    return Recipe(
        title = this.title,
        ingredientsUsed = this.ingredientsUsed,
        instructions = this.instructions,
        duration = this.duration,
        difficulty = this.difficulty
    )
}