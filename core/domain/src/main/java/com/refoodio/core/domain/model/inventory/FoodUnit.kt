package com.refoodio.core.domain.model.inventory

import com.refoodio.core.domain.R

enum class FoodUnit(
    val id: Int,
    val shortNameResId: Int,
    val fullNameResId: Int,
    val min: Double,
    val max: Double,
    val step: Double,
    val type: UnitType
) {
    GRAM(1, R.string.unit_g_short, R.string.unit_gram_full, 0.0, 1000.0, 10.0, UnitType.WEIGHT),
    KILOGRAM(2, R.string.unit_kg_short, R.string.unit_kilogram_full, 0.1, 20.0, 1.0, UnitType.WEIGHT),
    LITER(3, R.string.unit_l_short, R.string.unit_liter_full, 0.1, 20.0, 1.0, UnitType.VOLUME),
    MILLILITER(4, R.string.unit_ml_short, R.string.unit_milliliter_full, 0.0, 1000.0, 10.0, UnitType.VOLUME),
    PIECE(5, R.string.unit_pc_short, R.string.unit_piece_full, 1.0, 100.0, 1.0, UnitType.COUNTABLE),
    BUNCH(6, R.string.unit_bunch_short, R.string.unit_bunch_full, 1.0, 20.0, 1.0, UnitType.COUNTABLE),
    PACK(7, R.string.unit_pack_short, R.string.unit_pack_full, 1.0, 50.0, 1.0, UnitType.COUNTABLE),
    CUP(8, R.string.unit_cup_short, R.string.unit_cup_full, 0.25, 10.0, 0.25, UnitType.VOLUME),
    TABLESPOON(9, R.string.unit_tbsp_short, R.string.unit_tablespoon_full, 0.5, 20.0, 0.5, UnitType.SPOON),
    TEASPOON(10, R.string.unit_tsp_short, R.string.unit_teaspoon_full, 0.5, 20.0, 0.5, UnitType.SPOON),
    TEA_SPOON(11, R.string.unit_chay_short, R.string.unit_chay_full, 0.5, 50.0, 0.5, UnitType.SPOON);

    enum class UnitType {
        WEIGHT,   // gr, kg
        VOLUME,   // lt, ml, bardak
        COUNTABLE, // adet, demet, paket
        SPOON     // kaşıklar
    }

    fun generateScale(): List<Double> {
        return when (this) {
            KILOGRAM, LITER, GRAM, MILLILITER -> {
                (min.toInt()..max.toInt()).map { it.toDouble() }
            }
            CUP, TABLESPOON, TEASPOON, TEA_SPOON -> {
                val list = mutableListOf<Double>()
                var current = min
                while (current <= max + 0.001) {
                    list.add(Math.round(current * 100) / 100.0)
                    current += step
                }
                list
            }
            else -> (min.toInt()..max.toInt()).map { it.toDouble() }
        }
    }

    companion object {
        fun fromId(id: Int): FoodUnit = entries.find { it.id == id } ?: PIECE
    }
}