package com.example.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class AppCategory(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector
) {
    FINANCE(
        id = "finance",
        title = "Finance Calculators",
        subtitle = "Loans, SIP, deposits, retirement & interest",
        icon = Icons.Default.AccountBalance
    ),
    BUSINESS_TAX(
        id = "business_tax",
        title = "Tax & Business",
        subtitle = "GST, Income Tax, margins, markup & discounts",
        icon = Icons.Default.ReceiptLong
    ),
    SCIENTIFIC(
        id = "scientific",
        title = "Scientific Calculator",
        subtitle = "Trigonometry, logarithms, powers & degree/radian",
        icon = Icons.Default.Functions
    ),
    MATH(
        id = "math",
        title = "Mathematics",
        subtitle = "Percentage, fractions, quadratic, geometry & stats",
        icon = Icons.Default.Calculate
    ),
    MASS_WEIGHT(
        id = "mass_weight",
        title = "Mass & Weight",
        subtitle = "Kilograms, pounds, ounces, grams & metric tonnes",
        icon = Icons.Default.Scale
    ),
    MEASUREMENT(
        id = "measurement",
        title = "Measurement Converters",
        subtitle = "Length, area, volume, speed, temperature & data",
        icon = Icons.Default.SquareFoot
    ),
    HEALTH(
        id = "health",
        title = "Health & Fitness",
        subtitle = "BMI, BMR, daily water, calories & ideal weight",
        icon = Icons.Default.FitnessCenter
    ),
    DATE_TIME(
        id = "date_time",
        title = "Date & Time",
        subtitle = "Age calculator, date differences, countdowns",
        icon = Icons.Default.CalendarMonth
    ),
    CONSTRUCTION(
        id = "construction",
        title = "Construction",
        subtitle = "Concrete, bricks, tiles, paint & steel weight",
        icon = Icons.Default.Construction
    )
}

enum class CalculatorId(
    val title: String,
    val shortDesc: String,
    val category: AppCategory,
    val keywords: List<String>,
    val formulaSummary: String,
    val isPopular: Boolean = false,
    val isFeatured: Boolean = false
) {
    // Finance
    EMI_CALCULATOR("EMI Calculator", "Calculate monthly loan installments & amortization", AppCategory.FINANCE, listOf("emi", "loan", "monthly payment", "interest"), "E = P × r × (1 + r)^n / ((1 + r)^n - 1)", isPopular = true, isFeatured = true),
    HOME_LOAN("Home Loan Calculator", "Affordability, EMI & tax benefits on home loans", AppCategory.FINANCE, listOf("home loan", "housing", "mortgage", "property"), "Standard amortized loan calculation"),
    CAR_LOAN("Car Loan Calculator", "Vehicle financing EMI with down payment", AppCategory.FINANCE, listOf("car loan", "auto", "vehicle"), "E = P × r × (1+r)^n / ((1+r)^n - 1)"),
    PERSONAL_LOAN("Personal Loan Calculator", "Quick unsecured personal loan estimation", AppCategory.FINANCE, listOf("personal", "instant loan", "cash"), "Reducing balance EMI formula"),
    BIKE_LOAN("Two-Wheeler Loan", "Bike & scooter financing monthly installments", AppCategory.FINANCE, listOf("bike", "scooter", "two wheeler"), "Standard reducing balance EMI"),
    SIMPLE_INTEREST("Simple Interest", "Calculate linear interest on principal sum", AppCategory.FINANCE, listOf("si", "simple interest", "borrowing"), "SI = (P × R × T) / 100", isPopular = true),
    COMPOUND_INTEREST("Compound Interest", "Annual, quarterly or monthly compounding growth", AppCategory.FINANCE, listOf("ci", "compound interest", "growth"), "A = P(1 + r/n)^(nt)", isPopular = true),
    SIP_CALCULATOR("SIP Calculator", "Systematic Investment Plan mutual fund returns", AppCategory.FINANCE, listOf("sip", "mutual fund", "monthly investment", "wealth"), "M = P × ({[1 + i]^n - 1} / i) × (1 + i)", isPopular = true),
    LUMPSUM_CALCULATOR("Lumpsum Investment", "One-time mutual fund / stock returns", AppCategory.FINANCE, listOf("lumpsum", "investment", "wealth"), "A = P × (1 + r)^n"),
    FD_CALCULATOR("FD Calculator", "Fixed Deposit maturity value & interest", AppCategory.FINANCE, listOf("fd", "fixed deposit", "bank interest"), "A = P × (1 + r/n)^(nt)"),
    RD_CALCULATOR("RD Calculator", "Recurring Deposit maturity amount", AppCategory.FINANCE, listOf("rd", "recurring deposit", "post office"), "Maturity formula based on quarterly compounding"),
    PPF_CALCULATOR("PPF Calculator", "Public Provident Fund 15-year wealth builder", AppCategory.FINANCE, listOf("ppf", "provident fund", "tax saving", "7.1%"), "Government backed 15-year compounding (7.1% p.a.)"),
    EPF_CALCULATOR("EPF Calculator", "Employee Provident Fund corpus at retirement", AppCategory.FINANCE, listOf("epf", "pf", "salary pf", "retirement corpus"), "12% employee + 3.67% employer EPF accumulation"),
    RETIREMENT_PLANNER("Retirement Calculator", "Corpus needed to retire comfortably", AppCategory.FINANCE, listOf("retirement", "pension", "fire", "nest egg"), "Inflation adjusted annuity calculation"),
    INFLATION_CALCULATOR("Inflation Calculator", "Future purchasing power & cost of living", AppCategory.FINANCE, listOf("inflation", "cpi", "purchasing power", "value"), "Future Value = PV × (1 + i)^n"),
    CAGR_CALCULATOR("CAGR Calculator", "Compound Annual Growth Rate", AppCategory.FINANCE, listOf("cagr", "growth rate", "annual return"), "CAGR = (Ending / Beginning)^(1/n) - 1"),
    ROI_CALCULATOR("ROI Calculator", "Return on Investment & percentage gain", AppCategory.FINANCE, listOf("roi", "return", "profitability"), "ROI = ((Gain - Cost) / Cost) × 100%"),
    SALARY_IN_HAND("In-Hand Salary Calculator", "Take-home pay after PF, taxes & deductions", AppCategory.FINANCE, listOf("salary", "ctc", "take home", "in hand pay"), "Net = Gross - (PF + Tax + Deductions)"),
    CREDIT_CARD_INTEREST("Credit Card Payoff", "Interest and time to pay off card debt", AppCategory.FINANCE, listOf("credit card", "apr", "debt", "minimum payment"), "Monthly compounding debt amortization"),

    // Tax & Business
    GST_CALCULATOR("GST Calculator", "Calculate GST inclusive, exclusive & CGST/SGST", AppCategory.BUSINESS_TAX, listOf("gst", "tax", "vat", "cgst", "sgst", "igst"), "Inclusive: Tax = Total - (Total / (1 + r/100))", isPopular = true, isFeatured = true),
    INCOME_TAX_INDIA("Income Tax (India)", "Compare Old vs New Tax Regime with deductions", AppCategory.BUSINESS_TAX, listOf("income tax", "tds", "new regime", "old regime", "80c"), "Finance Act 2024 / FY 2024-25 Slabs + 4% Cess", isPopular = true),
    TDS_CALCULATOR("TDS Calculator", "Tax Deducted at Source for contracts & rent", AppCategory.BUSINESS_TAX, listOf("tds", "withholding", "tax deduction"), "TDS = Amount × TDS Rate%"),
    HRA_CALCULATOR("HRA Exemption", "House Rent Allowance tax exemption under Sec 10(13A)", AppCategory.BUSINESS_TAX, listOf("hra", "rent exemption", "tax saving"), "Min of: Actual HRA, 50%/40% of Basic, Rent - 10% Basic"),
    PROFIT_LOSS("Profit & Loss / Margin", "Calculate profit, margin %, markup & loss", AppCategory.BUSINESS_TAX, listOf("profit", "loss", "margin", "markup", "cost"), "Margin = (Profit / Selling Price) × 100%", isPopular = true),
    DISCOUNT_CALCULATOR("Discount Calculator", "Sale price, savings & multi-step discounts", AppCategory.BUSINESS_TAX, listOf("discount", "sale", "off", "coupon"), "Savings = Price × (Discount / 100)"),
    BREAK_EVEN("Break-Even Calculator", "Units & revenue needed to cover fixed costs", AppCategory.BUSINESS_TAX, listOf("breakeven", "break even", "fixed costs", "business"), "BEP = Fixed Costs / (Price - Variable Cost)"),
    COMMISSION_CALCULATOR("Commission & Revenue", "Agent commission & gross business revenue", AppCategory.BUSINESS_TAX, listOf("commission", "brokerage", "revenue"), "Commission = Sale Amount × Commission%"),

    // Scientific & Math
    SCIENTIFIC_CALC("Scientific Calculator", "Trigonometric, logarithmic & exponential keypad", AppCategory.SCIENTIFIC, listOf("scientific", "sin", "cos", "tan", "log", "ln", "power"), "Handheld scientific math engine", isPopular = true),
    BASIC_CALC("Standard Calculator", "Quick everyday arithmetic with memory keys", AppCategory.MATH, listOf("calculator", "math", "basic", "add"), "Standard arithmetic"),
    PERCENTAGE_CALCULATOR("Percentage Calculator", "X% of Y, percentage change, increase/decrease", AppCategory.MATH, listOf("percent", "percentage", "increase", "ratio"), "Result = (Value × Percent) / 100", isPopular = true),
    FRACTION_CALCULATOR("Fraction Calculator", "Add, subtract, multiply, divide & simplify", AppCategory.MATH, listOf("fraction", "numerator", "denominator", "simplify"), "Exact fractional arithmetic & reduction"),
    RATIO_CALCULATOR("Ratio & Proportion", "Solve A:B = C:D, divide quantities by ratio", AppCategory.MATH, listOf("ratio", "proportion", "scale", "divide"), "A/B = C/D cross multiplication"),
    AVERAGE_CALCULATOR("Average & Statistics", "Mean, median, mode, range & sum", AppCategory.MATH, listOf("average", "mean", "median", "mode", "stats"), "Mean = Sum / Count"),
    QUADRATIC_SOLVER("Quadratic Equation", "Roots of ax² + bx + c = 0 with step-by-step", AppCategory.MATH, listOf("quadratic", "roots", "algebra", "polynomial"), "x = (-b ± √(b² - 4ac)) / (2a)"),
    GEOMETRY_CALCULATOR("Geometry (Area & Volume)", "2D & 3D geometric shape dimensions", AppCategory.MATH, listOf("geometry", "area", "perimeter", "volume", "circle", "triangle"), "Standard Euclidean geometric formulas"),
    PROBABILITY_STATS("Permutation & Combination", "nPr, nCr, factorial and sample space", AppCategory.MATH, listOf("probability", "npr", "ncr", "permutation", "combination"), "nCr = n! / (r! × (n-r)!)"),

    // Mass & Weight
    MASS_WEIGHT_CONVERTER("Mass & Weight Converter", "Convert kg, g, mg, lb, oz, stone, tonne, carat", AppCategory.MASS_WEIGHT, listOf("weight", "mass", "kg", "lbs", "pounds", "grams", "ounce"), "Base SI kilogram conversion factor", isPopular = true),

    // Measurements
    LENGTH_CONVERTER("Length Converter", "Kilometers, meters, feet, inches, miles, yards", AppCategory.MEASUREMENT, listOf("length", "distance", "meters", "feet", "miles", "inch"), "Base meter conversion"),
    AREA_CONVERTER("Area Converter", "Square meters, sq ft, acres, hectares, sq km", AppCategory.MEASUREMENT, listOf("area", "acre", "hectare", "sq ft", "land"), "Base square meter conversion"),
    VOLUME_CONVERTER("Volume Converter", "Liters, gallons, cubic meters, cups, ml, fl oz", AppCategory.MEASUREMENT, listOf("volume", "liquid", "liters", "gallons", "cup"), "Base cubic meter / liter conversion"),
    TEMPERATURE_CONVERTER("Temperature Converter", "Celsius, Fahrenheit, and Kelvin conversion", AppCategory.MEASUREMENT, listOf("temperature", "celsius", "fahrenheit", "kelvin", "weather"), "°F = (°C × 9/5) + 32; K = °C + 273.15"),
    SPEED_CONVERTER("Speed Converter", "km/h, mph, m/s, knots, mach", AppCategory.MEASUREMENT, listOf("speed", "velocity", "kmh", "mph", "knots"), "Base meters per second conversion"),
    TIME_CONVERTER("Time Unit Converter", "Seconds, minutes, hours, days, weeks, years", AppCategory.MEASUREMENT, listOf("time", "seconds", "hours", "days", "duration"), "Base seconds conversion"),
    DATA_STORAGE_CONVERTER("Digital Storage", "Bits, Bytes, KB, MB, GB, TB, PB (Decimal/Binary)", AppCategory.MEASUREMENT, listOf("data", "storage", "bytes", "mb", "gb", "tb"), "1 KB = 1024 Bytes / 1000 Bytes"),
    PRESSURE_CONVERTER("Pressure Converter", "Pascal, kPa, bar, psi, atmosphere, mmHg", AppCategory.MEASUREMENT, listOf("pressure", "bar", "psi", "pascal", "atm"), "Base Pascal conversion"),
    ENERGY_POWER_CONVERTER("Energy & Power", "Joules, calories, kWh, Watts, horsepower", AppCategory.MEASUREMENT, listOf("energy", "power", "joule", "watt", "hp", "calories"), "Base Joules and Watts conversions"),

    // Health & Fitness
    BMI_CALCULATOR("BMI Calculator", "Body Mass Index & WHO weight categorization", AppCategory.HEALTH, listOf("bmi", "weight", "obesity", "underweight", "height"), "BMI = weight (kg) / height² (m)", isPopular = true),
    BMR_CALCULATOR("BMR Calculator", "Basal Metabolic Rate via Mifflin-St Jeor formula", AppCategory.HEALTH, listOf("bmr", "metabolism", "calories", "metabolic rate"), "BMR = 10W + 6.25H - 5A + S (gender constant)"),
    DAILY_WATER_INTAKE("Daily Water Intake", "Hydration needs based on weight & activity", AppCategory.HEALTH, listOf("water", "hydration", "fluid", "health"), "Baseline 35ml/kg + 350ml per 30m exercise"),
    CALORIE_REQUIREMENT("Calorie Estimator", "Maintenance, deficit & surplus daily targets", AppCategory.HEALTH, listOf("calorie", "tdee", "diet", "weight loss", "gain"), "TDEE = BMR × Physical Activity Factor"),
    IDEAL_WEIGHT("Ideal Body Weight", "Healthy weight range via Devine & Robinson formulas", AppCategory.HEALTH, listOf("ideal weight", "healthy weight", "target weight"), "Devine formula: 50kg + 2.3kg per inch over 5ft"),

    // Date & Time
    AGE_CALCULATOR("Age Calculator", "Exact age in years, months, days, minutes & seconds", AppCategory.DATE_TIME, listOf("age", "birthday", "how old", "chronological"), "Exact calendar period difference", isPopular = true),
    DATE_DIFFERENCE("Date Difference", "Calculate exact days, weeks & months between dates", AppCategory.DATE_TIME, listOf("date diff", "days between", "duration", "calendar"), "Gregorian calendar delta calculation"),
    COUNTDOWN_TIMER("Birthday & Event Countdown", "Days, hours & minutes until upcoming milestone", AppCategory.DATE_TIME, listOf("countdown", "days until", "birthday countdown", "event"), "Target instant - current instant delta"),
    WORKDAYS_CALCULATOR("Workdays Calculator", "Business working days excluding weekends", AppCategory.DATE_TIME, listOf("workdays", "business days", "working days", "leave"), "Iterative weekday filtering excluding Saturday/Sunday"),
    DATE_ADD_SUBTRACT("Add / Subtract Days", "Find future or past date by adding days/months", AppCategory.DATE_TIME, listOf("date math", "add days", "due date"), "Calendar addition/subtraction"),

    // Construction
    CONCRETE_CEMENT("Concrete & Cement Mix", "Bags of cement, sand & aggregate required", AppCategory.CONSTRUCTION, listOf("concrete", "cement", "sand", "aggregate", "slab"), "Wet Volume × 1.54 dry factor ÷ ratio sum", isPopular = true),
    BRICK_CALCULATOR("Brick & Mortar Calculator", "Total standard bricks & cement mortar for wall", AppCategory.CONSTRUCTION, listOf("brick", "masonry", "wall", "construction"), "Wall volume ÷ single brick volume + 10% wastage"),
    TILE_CALCULATOR("Flooring Tile Calculator", "Total tiles and box count with wastage allowance", AppCategory.CONSTRUCTION, listOf("tiles", "flooring", "room area", "ceramics"), "(Room Area / Tile Area) × 1.10 (wastage)"),
    PAINT_CALCULATOR("Paint Calculator", "Liters of paint required for walls & ceiling", AppCategory.CONSTRUCTION, listOf("paint", "wall paint", "coverage", "coating"), "(Total Wall Area - Openings) × Coats ÷ Coverage"),
    STEEL_WEIGHT("Steel Bar Weight", "Reinforcement TMT rebar weight by diameter", AppCategory.CONSTRUCTION, listOf("steel", "tmt", "rebar", "iron rod", "weight"), "Weight (kg/m) = (Diameter in mm)² / 162")
}

data class CalculationResult(
    val primaryValue: String,
    val primaryLabel: String,
    val secondarySummary: String? = null,
    val breakdown: List<Pair<String, String>> = emptyList(),
    val formulaUsed: String? = null,
    val explanation: String? = null,
    val chartData: ChartData? = null
)

sealed class ChartData {
    data class DonutOrPie(
        val segments: List<ChartSegment>
    ) : ChartData()

    data class ComparisonBars(
        val bars: List<BarItem>
    ) : ChartData()

    data class ScaleGauge(
        val currentValue: Float,
        val minValue: Float,
        val maxValue: Float,
        val markers: List<Pair<Float, String>>,
        val currentLabel: String
    ) : ChartData()
}

data class ChartSegment(
    val label: String,
    val value: Double,
    val colorHex: Long
)

data class BarItem(
    val label: String,
    val value: Double,
    val displayValue: String,
    val colorHex: Long
)

data class HistoryItem(
    val id: Long = 0,
    val calculatorId: String,
    val title: String,
    val categoryName: String,
    val resultSummary: String,
    val inputsSummary: String,
    val timestamp: Long = System.currentTimeMillis()
)
