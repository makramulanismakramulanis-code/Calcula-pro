package com.example.domain

import com.example.data.model.CalculationResult
import kotlin.math.ceil

object ConstructionEngine {

    enum class ConcreteGrade(val cementPart: Double, val sandPart: Double, val aggregatePart: Double, val label: String) {
        M15(1.0, 2.0, 4.0, "M15 (1 : 2 : 4) - Patios, Footpaths"),
        M20(1.0, 1.5, 3.0, "M20 (1 : 1.5 : 3) - Standard Slabs, Beams"),
        M25(1.0, 1.0, 2.0, "M25 (1 : 1 : 2) - Heavy Columns, Retaining Walls")
    }

    fun calculateConcrete(
        lengthM: Double,
        widthM: Double,
        depthM: Double,
        grade: ConcreteGrade
    ): CalculationResult {
        if (lengthM <= 0 || widthM <= 0 || depthM <= 0) {
            return CalculationResult("0 Bags", "Cement Required", "Enter valid dimensions")
        }

        val wetVolumeCuM = lengthM * widthM * depthM
        // Concrete dry volume factor is 1.54
        val dryVolumeCuM = wetVolumeCuM * 1.54
        val sumRatio = grade.cementPart + grade.sandPart + grade.aggregatePart

        val cementVolumeCuM = (grade.cementPart / sumRatio) * dryVolumeCuM
        val sandVolumeCuM = (grade.sandPart / sumRatio) * dryVolumeCuM
        val aggregateVolumeCuM = (grade.aggregatePart / sumRatio) * dryVolumeCuM

        // 1 cubic meter of cement = 1440 kg = 28.8 bags of 50kg
        val cementBags = cementVolumeCuM * 28.8
        // 1 cu m = 35.3147 cubic feet
        val sandCuFt = sandVolumeCuM * 35.3147
        val aggregateCuFt = aggregateVolumeCuM * 35.3147
        val waterLiters = cementBags * 25.0 // ~25-28 liters per bag for w/c 0.50

        val bagsF = "${NumberFormatter.formatNumber(ceil(cementBags), 0)} Bags"
        val volF = "${NumberFormatter.formatNumber(wetVolumeCuM, 2)} m³"

        return CalculationResult(
            primaryValue = bagsF,
            primaryLabel = "Cement Bags (50kg each)",
            secondarySummary = "Wet Volume: $volF | Sand: ${NumberFormatter.formatNumber(sandCuFt, 0)} cu ft",
            breakdown = listOf(
                "Total Wet Volume" to "$volF (${NumberFormatter.formatNumber(wetVolumeCuM * 35.3147, 1)} cu ft)",
                "Dry Volume (1.54 factor)" to "${NumberFormatter.formatNumber(dryVolumeCuM, 2)} m³",
                "Cement Required" to "$bagsF (${NumberFormatter.formatNumber(cementBags * 50.0, 0)} kg)",
                "Sand (Fine Aggregate)" to "${NumberFormatter.formatNumber(sandCuFt, 1)} cu ft (${NumberFormatter.formatNumber(sandVolumeCuM * 1600.0 / 1000.0, 2)} tonnes)",
                "Coarse Aggregate (Gravel)" to "${NumberFormatter.formatNumber(aggregateCuFt, 1)} cu ft (${NumberFormatter.formatNumber(aggregateVolumeCuM * 1500.0 / 1000.0, 2)} tonnes)",
                "Estimated Water Needed" to "${NumberFormatter.formatNumber(waterLiters, 0)} Liters",
                "Selected Mix Grade" to grade.label
            ),
            formulaUsed = "Dry Volume = Wet Volume × 1.54\nComponent Volume = (Ratio Part / Total Ratio) × Dry Volume\nCement Bags = Cement Volume (m³) × 28.8 bags",
            explanation = "Assumes dry density of cement at 1440 kg/m³, standard 50 kg bags, and dry mix swelling factor of 1.54 to account for voids."
        )
    }

    fun calculateBricks(
        wallLengthM: Double,
        wallHeightM: Double,
        wallThicknessMm: Double = 230.0, // 230mm (9") or 115mm (4.5")
        deductionAreaSqM: Double = 0.0
    ): CalculationResult {
        if (wallLengthM <= 0 || wallHeightM <= 0) {
            return CalculationResult("0 Bricks", "Bricks Required", "Enter valid wall dimensions")
        }

        val wallGrossArea = wallLengthM * wallHeightM
        val netArea = (wallGrossArea - deductionAreaSqM).coerceAtLeast(0.0)
        val wallThicknessM = wallThicknessMm / 1000.0
        val wallVolume = netArea * wallThicknessM

        // Standard brick with mortar in India/Commonwealth is 200mm × 100mm × 100mm = 0.002 m³
        // Approximately 500 bricks per cubic meter of brickwork
        val baseBricks = wallVolume * 500.0
        val withWastage = baseBricks * 1.10 // 10% wastage

        val totalCount = ceil(withWastage).toInt()
        val mortarVolume = wallVolume * 0.30 // ~30% mortar
        val cementBagsForMortar = ceil(mortarVolume * 0.25 * 28.8) // 1:5 mortar

        return CalculationResult(
            primaryValue = "${NumberFormatter.formatNumber(totalCount.toDouble(), 0)} Bricks",
            primaryLabel = "Total Bricks Required",
            secondarySummary = "Includes 10% cutting wastage allowance (Wall: ${NumberFormatter.formatNumber(netArea, 1)} m²)",
            breakdown = listOf(
                "Estimated Bricks (with 10% waste)" to "${NumberFormatter.formatNumber(totalCount.toDouble(), 0)} bricks",
                "Net Brickwork Volume" to "${NumberFormatter.formatNumber(wallVolume, 2)} m³",
                "Wall Surface Area" to "${NumberFormatter.formatNumber(netArea, 2)} m² (${NumberFormatter.formatNumber(netArea * 10.7639, 1)} sq ft)",
                "Mortar Volume (approx 30%)" to "${NumberFormatter.formatNumber(mortarVolume, 2)} m³",
                "Mortar Cement (1:5 mix)" to "~${NumberFormatter.formatNumber(cementBagsForMortar, 0)} bags (50kg)"
            ),
            formulaUsed = "Total Bricks = (Wall Volume m³ × 500 bricks/m³) × 1.10 (wastage)",
            explanation = "Standard modular brick nominal dimensions with 10mm mortar joints (200 × 100 × 100 mm). Includes 10% extra for breakage and cuts."
        )
    }

    fun calculateTiles(
        roomLengthM: Double,
        roomWidthM: Double,
        tileLengthMm: Double = 600.0,
        tileWidthMm: Double = 600.0,
        tilesPerBox: Int = 4
    ): CalculationResult {
        if (roomLengthM <= 0 || roomWidthM <= 0 || tileLengthMm <= 0 || tileWidthMm <= 0) {
            return CalculationResult("0 Tiles", "Tiles Required", "Enter valid dimensions")
        }

        val roomAreaSqM = roomLengthM * roomWidthM
        val tileAreaSqM = (tileLengthMm / 1000.0) * (tileWidthMm / 1000.0)

        val netTiles = roomAreaSqM / tileAreaSqM
        val withWastage = netTiles * 1.10 // 10% cutting and skirting wastage
        val totalTiles = ceil(withWastage).toInt()
        val boxes = ceil(totalTiles.toDouble() / tilesPerBox.toDouble()).toInt()

        return CalculationResult(
            primaryValue = "$totalTiles Tiles",
            primaryLabel = "Total Tiles Needed",
            secondarySummary = "Order $boxes Boxes ($tilesPerBox tiles/box, 10% wastage included)",
            breakdown = listOf(
                "Total Tiles Count" to "$totalTiles tiles",
                "Recommended Boxes" to "$boxes boxes",
                "Floor Area" to "${NumberFormatter.formatNumber(roomAreaSqM, 2)} m² (${NumberFormatter.formatNumber(roomAreaSqM * 10.7639, 1)} sq ft)",
                "Single Tile Size" to "${NumberFormatter.formatNumber(tileLengthMm, 0)} × ${NumberFormatter.formatNumber(tileWidthMm, 0)} mm",
                "Single Tile Area" to "${NumberFormatter.formatNumber(tileAreaSqM, 3)} m²",
                "Wastage Factor" to "10% included for corner cuts and skirting"
            ),
            formulaUsed = "Total Tiles = (Floor Area / Tile Area) × 1.10\nBoxes = Ceil(Total Tiles / Tiles per Box)",
            explanation = "A 10% contingency covers corner trimming, threshold transitions, and replacement reserves from the same production dye batch."
        )
    }

    fun calculatePaint(
        wallLengthM: Double,
        wallWidthM: Double,
        wallHeightM: Double,
        coats: Int = 2,
        openingsSqM: Double = 0.0
    ): CalculationResult {
        if (wallLengthM <= 0 || wallWidthM <= 0 || wallHeightM <= 0) {
            return CalculationResult("0 L", "Paint Required", "Enter valid room dimensions")
        }

        // Perimeter = 2 * (L + W), Wall Area = Perimeter * H
        val perimeter = 2.0 * (wallLengthM + wallWidthM)
        val grossWallArea = perimeter * wallHeightM
        val netWallArea = (grossWallArea - openingsSqM).coerceAtLeast(0.0)

        // Standard emulsion paint coverage is approx 11-13 sq meters per liter per coat (approx 120 sq ft/L)
        val coveragePerLiter = 11.0
        val litersNeeded = (netWallArea * coats) / coveragePerLiter

        val litF = "${NumberFormatter.formatNumber(litersNeeded, 1)} Liters"

        return CalculationResult(
            primaryValue = litF,
            primaryLabel = "Total Paint Required",
            secondarySummary = "For $coats coats of paint covering ${NumberFormatter.formatNumber(netWallArea, 1)} m² wall surface",
            breakdown = listOf(
                "Total Paint Quantity" to litF,
                "Number of Coats" to "$coats Coats",
                "Net Wall Surface Area" to "${NumberFormatter.formatNumber(netWallArea, 1)} m² (${NumberFormatter.formatNumber(netWallArea * 10.7639, 1)} sq ft)",
                "Deducted Doors & Windows" to "${NumberFormatter.formatNumber(openingsSqM, 1)} m²",
                "Average Paint Coverage" to "11 m² (120 sq ft) per Liter per coat"
            ),
            formulaUsed = "Paint Liters = (Net Wall Surface Area × Coats) ÷ Coverage per Liter",
            explanation = "Standard acrylic interior emulsion coverage on properly primed surfaces. Highly porous fresh plaster may absorb 15% more primer."
        )
    }

    fun calculateSteelWeight(diameterMm: Double, lengthM: Double): CalculationResult {
        if (diameterMm <= 0 || lengthM <= 0) {
            return CalculationResult("0 kg", "Steel Weight", "Enter valid diameter and length")
        }

        // Standard civil engineering formula: W = (D^2 / 162) * L kg
        val weightPerMeter = (diameterMm * diameterMm) / 162.0
        val totalWeightKg = weightPerMeter * lengthM
        val totalTonnes = totalWeightKg / 1000.0

        val kgF = "${NumberFormatter.formatNumber(totalWeightKg, 2)} kg"

        return CalculationResult(
            primaryValue = kgF,
            primaryLabel = "Reinforcement Steel Weight",
            secondarySummary = "Weight per meter: ${NumberFormatter.formatNumber(weightPerMeter, 3)} kg/m",
            breakdown = listOf(
                "Total Weight in Kilograms" to kgF,
                "Total Weight in Quintals" to "${NumberFormatter.formatNumber(totalWeightKg / 100.0, 2)} Quintals",
                "Total Weight in Metric Tonnes" to "${NumberFormatter.formatNumber(totalTonnes, 3)} Tonnes",
                "TMT Bar Diameter" to "${NumberFormatter.formatNumber(diameterMm, 1)} mm",
                "Total Running Length" to "${NumberFormatter.formatNumber(lengthM, 1)} Meters",
                "Unit Weight Formula" to "D² / 162 kg/m"
            ),
            formulaUsed = "Unit Weight (kg/m) = Diameter² (mm) ÷ 162\nTotal Weight (kg) = Unit Weight × Length (m)",
            explanation = "Standard civil construction steel formula derived from density of carbon steel (7850 kg/m³)."
        )
    }
}
