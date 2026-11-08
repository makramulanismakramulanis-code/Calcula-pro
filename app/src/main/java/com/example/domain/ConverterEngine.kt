package com.example.domain

import com.example.data.model.CalculationResult

data class UnitDefinition(
    val id: String,
    val name: String,
    val symbol: String,
    val factorToBase: Double // Multiplied by value in this unit to get base unit
)

enum class UnitCategory(val title: String, val baseUnitName: String) {
    MASS_WEIGHT("Mass & Weight", "Kilogram (kg)"),
    LENGTH("Length & Distance", "Metre (m)"),
    AREA("Area", "Square Metre (m²)"),
    VOLUME("Volume & Capacity", "Litre (L)"),
    TEMPERATURE("Temperature", "Celsius (°C)"),
    SPEED("Speed & Velocity", "Meters per second (m/s)"),
    TIME("Time Duration", "Second (s)"),
    DATA_STORAGE("Digital Data Storage", "Byte (B)"),
    PRESSURE("Pressure", "Pascal (Pa)"),
    ENERGY("Energy & Work", "Joule (J)"),
    POWER("Power", "Watt (W)")
}

object ConverterEngine {

    val massUnits = listOf(
        UnitDefinition("kg", "Kilogram", "kg", 1.0),
        UnitDefinition("g", "Gram", "g", 0.001),
        UnitDefinition("mg", "Milligram", "mg", 0.000001),
        UnitDefinition("ug", "Microgram", "µg", 0.000000001),
        UnitDefinition("t", "Metric Tonne", "t", 1000.0),
        UnitDefinition("lb", "Pound", "lb", 0.45359237),
        UnitDefinition("oz", "Ounce", "oz", 0.0283495231),
        UnitDefinition("st", "Stone", "st", 6.35029318),
        UnitDefinition("ct", "Carat", "ct", 0.0002)
    )

    val lengthUnits = listOf(
        UnitDefinition("km", "Kilometre", "km", 1000.0),
        UnitDefinition("m", "Metre", "m", 1.0),
        UnitDefinition("cm", "Centimetre", "cm", 0.01),
        UnitDefinition("mm", "Millimetre", "mm", 0.001),
        UnitDefinition("mi", "Mile", "mi", 1609.344),
        UnitDefinition("yd", "Yard", "yd", 0.9144),
        UnitDefinition("ft", "Foot", "ft", 0.3048),
        UnitDefinition("in", "Inch", "in", 0.0254)
    )

    val areaUnits = listOf(
        UnitDefinition("sq_m", "Square Metre", "m²", 1.0),
        UnitDefinition("sq_km", "Square Kilometre", "km²", 1000000.0),
        UnitDefinition("sq_ft", "Square Foot", "sq ft", 0.09290304),
        UnitDefinition("sq_yd", "Square Yard", "sq yd", 0.83612736),
        UnitDefinition("acre", "Acre", "ac", 4046.85642),
        UnitDefinition("hectare", "Hectare", "ha", 10000.0)
    )

    val volumeUnits = listOf(
        UnitDefinition("l", "Litre", "L", 1.0),
        UnitDefinition("ml", "Millilitre", "mL", 0.001),
        UnitDefinition("cu_m", "Cubic Metre", "m³", 1000.0),
        UnitDefinition("gal", "US Gallon", "gal", 3.78541178),
        UnitDefinition("cup", "US Cup", "cup", 0.23658823),
        UnitDefinition("tbsp", "Tablespoon", "tbsp", 0.01478676),
        UnitDefinition("tsp", "Teaspoon", "tsp", 0.00492892),
        UnitDefinition("fl_oz", "Fluid Ounce", "fl oz", 0.0295735)
    )

    val speedUnits = listOf(
        UnitDefinition("kmh", "Kilometres per hour", "km/h", 1.0 / 3.6),
        UnitDefinition("mph", "Miles per hour", "mph", 0.44704),
        UnitDefinition("ms", "Metres per second", "m/s", 1.0),
        UnitDefinition("knot", "Knot (Nautical mi/h)", "kn", 0.514444)
    )

    val timeUnits = listOf(
        UnitDefinition("s", "Second", "s", 1.0),
        UnitDefinition("min", "Minute", "min", 60.0),
        UnitDefinition("h", "Hour", "h", 3600.0),
        UnitDefinition("d", "Day", "d", 86400.0),
        UnitDefinition("wk", "Week", "wk", 604800.0),
        UnitDefinition("mo", "Month (Avg 30.4d)", "mo", 2629746.0),
        UnitDefinition("yr", "Year (365.25d)", "yr", 31557600.0)
    )

    val dataStorageUnits = listOf(
        UnitDefinition("b", "Bit", "b", 0.125),
        UnitDefinition("B", "Byte", "B", 1.0),
        UnitDefinition("KB", "Kilobyte (1024 B)", "KB", 1024.0),
        UnitDefinition("MB", "Megabyte", "MB", 1024.0 * 1024.0),
        UnitDefinition("GB", "Gigabyte", "GB", 1024.0 * 1024.0 * 1024.0),
        UnitDefinition("TB", "Terabyte", "TB", 1024.0 * 1024.0 * 1024.0 * 1024.0),
        UnitDefinition("PB", "Petabyte", "PB", 1024.0 * 1024.0 * 1024.0 * 1024.0 * 1024.0)
    )

    val pressureUnits = listOf(
        UnitDefinition("pa", "Pascal", "Pa", 1.0),
        UnitDefinition("kpa", "Kilopascal", "kPa", 1000.0),
        UnitDefinition("bar", "Bar", "bar", 100000.0),
        UnitDefinition("psi", "Pound per sq in", "psi", 6894.75729),
        UnitDefinition("atm", "Standard Atmosphere", "atm", 101325.0)
    )

    val energyUnits = listOf(
        UnitDefinition("j", "Joule", "J", 1.0),
        UnitDefinition("kj", "Kilojoule", "kJ", 1000.0),
        UnitDefinition("cal", "Calorie", "cal", 4.184),
        UnitDefinition("kcal", "Kilocalorie (Food cal)", "kcal", 4184.0),
        UnitDefinition("kwh", "Kilowatt-hour", "kWh", 3600000.0),
        UnitDefinition("btu", "British Thermal Unit", "BTU", 1055.06)
    )

    val powerUnits = listOf(
        UnitDefinition("w", "Watt", "W", 1.0),
        UnitDefinition("kw", "Kilowatt", "kW", 1000.0),
        UnitDefinition("hp", "Mechanical Horsepower", "hp", 745.699872),
        UnitDefinition("mw", "Megawatt", "MW", 1000000.0)
    )

    fun getUnitsForCategory(category: UnitCategory): List<UnitDefinition> {
        return when (category) {
            UnitCategory.MASS_WEIGHT -> massUnits
            UnitCategory.LENGTH -> lengthUnits
            UnitCategory.AREA -> areaUnits
            UnitCategory.VOLUME -> volumeUnits
            UnitCategory.SPEED -> speedUnits
            UnitCategory.TIME -> timeUnits
            UnitCategory.DATA_STORAGE -> dataStorageUnits
            UnitCategory.PRESSURE -> pressureUnits
            UnitCategory.ENERGY -> energyUnits
            UnitCategory.POWER -> powerUnits
            UnitCategory.TEMPERATURE -> emptyList() // Handled specially
        }
    }

    fun convertStandard(
        value: Double,
        fromUnit: UnitDefinition,
        toUnit: UnitDefinition
    ): CalculationResult {
        val baseValue = value * fromUnit.factorToBase
        val converted = baseValue / toUnit.factorToBase

        val resultF = NumberFormatter.formatNumber(converted, 4)
        val inputF = NumberFormatter.formatNumber(value, 4)

        return CalculationResult(
            primaryValue = "$resultF ${toUnit.symbol}",
            primaryLabel = "${fromUnit.name} to ${toUnit.name}",
            secondarySummary = "$inputF ${fromUnit.symbol} = $resultF ${toUnit.symbol}",
            breakdown = listOf(
                "Source Input" to "$inputF ${fromUnit.symbol} (${fromUnit.name})",
                "Converted Result" to "$resultF ${toUnit.symbol} (${toUnit.name})",
                "Conversion Ratio" to "1 ${fromUnit.symbol} = ${NumberFormatter.formatNumber(fromUnit.factorToBase / toUnit.factorToBase, 6)} ${toUnit.symbol}"
            ),
            formulaUsed = "Target = (Source × ${fromUnit.factorToBase}) ÷ ${toUnit.factorToBase}"
        )
    }

    fun convertTemperature(
        value: Double,
        fromScale: String, // "C", "F", "K"
        toScale: String
    ): CalculationResult {
        // Convert input to Celsius first
        val celsius = when (fromScale) {
            "C" -> value
            "F" -> (value - 32.0) * (5.0 / 9.0)
            "K" -> value - 273.15
            else -> value
        }

        // Convert Celsius to target
        val converted = when (toScale) {
            "C" -> celsius
            "F" -> (celsius * (9.0 / 5.0)) + 32.0
            "K" -> celsius + 273.15
            else -> celsius
        }

        val fromSym = when (fromScale) { "C" -> "°C"; "F" -> "°F"; else -> "K" }
        val toSym = when (toScale) { "C" -> "°C"; "F" -> "°F"; else -> "K" }

        val resF = NumberFormatter.formatNumber(converted, 2)
        val inF = NumberFormatter.formatNumber(value, 2)

        val formula = when {
            fromScale == "C" && toScale == "F" -> "°F = (°C × 9/5) + 32"
            fromScale == "F" && toScale == "C" -> "°C = (°F - 32) × 5/9"
            fromScale == "C" && toScale == "K" -> "K = °C + 273.15"
            fromScale == "K" && toScale == "C" -> "°C = K - 273.15"
            fromScale == "F" && toScale == "K" -> "K = (°F - 32) × 5/9 + 273.15"
            fromScale == "K" && toScale == "F" -> "°F = (K - 273.15) × 9/5 + 32"
            else -> "Identical temperature scales"
        }

        return CalculationResult(
            primaryValue = "$resF $toSym",
            primaryLabel = "$inF $fromSym in $toSym",
            secondarySummary = "$inF $fromSym = $resF $toSym",
            breakdown = listOf(
                "Input Temperature" to "$inF $fromSym",
                "Celsius Equivalent" to "${NumberFormatter.formatNumber(celsius, 2)} °C",
                "Converted Target" to "$resF $toSym"
            ),
            formulaUsed = formula
        )
    }
}
