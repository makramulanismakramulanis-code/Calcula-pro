package com.example

import com.example.data.local.UserPreferences
import com.example.domain.*
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    private val prefs = UserPreferences()

    @Test
    fun testEmiCalculation() {
        // Loan: 10,00,000 at 8.5% for 20 years (240 months)
        val (result, schedule) = FinanceEngine.calculateEmi(1000000.0, 8.5, 240, prefs)
        assertTrue(result.primaryValue.isNotEmpty())
        assertEquals(240, schedule.size)
        // Verify balance decreases and final balance is 0
        assertEquals(0.0, schedule.last().remainingBalance, 0.5)
    }

    @Test
    fun testSimpleAndCompoundInterest() {
        val si = FinanceEngine.calculateSimpleInterest(100000.0, 10.0, 2.0, prefs)
        assertTrue(si.primaryValue.contains("20,000"))

        val ci = FinanceEngine.calculateCompoundInterest(100000.0, 10.0, 2.0, 1, prefs)
        assertTrue(ci.primaryValue.contains("1,21,000"))
    }

    @Test
    fun testGstCalculation() {
        val gstExclusive = TaxEngine.calculateGst(1000.0, 18.0, TaxEngine.GstType.EXCLUSIVE, false, prefs)
        assertTrue(gstExclusive.primaryValue.contains("1,180"))

        val gstInclusive = TaxEngine.calculateGst(1180.0, 18.0, TaxEngine.GstType.INCLUSIVE, false, prefs)
        assertTrue(gstInclusive.primaryValue.contains("180"))
    }

    @Test
    fun testScientificMathEvaluation() {
        val res = MathEngine.evaluateScientificExpression("2 + 3 * 4")
        assertEquals(14.0, res, 0.001)

        val sqrtVal = MathEngine.evaluateScientificExpression("sqrt(16) + 2^3")
        assertEquals(12.0, sqrtVal, 0.001)
    }

    @Test
    fun testUnitConversions() {
        val kg = ConverterEngine.massUnits.find { it.id == "kg" }!!
        val lb = ConverterEngine.massUnits.find { it.id == "lb" }!!
        val res = ConverterEngine.convertStandard(1.0, kg, lb)
        assertTrue(res.primaryValue.contains("2.2046"))
    }

    @Test
    fun testConcreteCalculation() {
        val slab = ConstructionEngine.calculateConcrete(5.0, 4.0, 0.15, ConstructionEngine.ConcreteGrade.M20)
        assertTrue(slab.primaryValue.contains("Bags"))
    }
}
