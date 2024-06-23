import java.io.File
import java.math.BigInteger

fun main() {
	val hailstones: MutableList<Hailstone> = mutableListOf()
	val lines = File("resources/day24Input.txt").readLines()

	lines.forEach { line ->
		val data = line.split("@")
		val coords = data[0].split(",")
		val velocity = data[1].split(",")
		val hailstone = Hailstone(
			x = coords[0].trim().toLong(), 
			y = coords[1].trim().toLong() , 
			z = coords[2].trim().toLong(), 
			xVelocity = velocity[0].trim().toLong(), 
			yVelocity = velocity[1].trim().toLong(), 
			zVelocity = velocity[2].trim().toLong()
		)
		hailstone.calculateSlope()
		hailstone.calculateIntercept()
		hailstones.add(hailstone)
	}

	println("Intersections: " + part1(hailstones, 200000000000000, 400000000000000))
	println("Sum of position components: " + part2(hailstones[0], hailstones[1], hailstones[2]))
}

private fun part1(hailstones: List<Hailstone>, lowerBound: Long, upperBound: Long): Long {
	var intersections: Long = 0
	for (i in 0..hailstones.lastIndex - 1) {
		for (j in i + 1..hailstones.lastIndex) {
			if (hailstones[i].calculateIntersection(hailstones[j], lowerBound, upperBound)) intersections++
		} 
	}

	return intersections
}

private fun part2(stone0: Hailstone, stone1: Hailstone, stone2: Hailstone): BigInteger {
	// heavily stolen from:
	// https://www.reddit.com/r/adventofcode/comments/18pnycy/comment/kxqjg33/?utm_source=share&utm_medium=web3x&utm_name=web3xcss&utm_term=1&utm_content=share_button

	// relative positions
	val p1 = listOf(BigInteger.valueOf(stone1.x - stone0.x), BigInteger.valueOf(stone1.y - stone0.y), BigInteger.valueOf(stone1.z - stone0.z))
	val p2 = listOf(BigInteger.valueOf(stone2.x - stone0.x), BigInteger.valueOf(stone2.y - stone0.y), BigInteger.valueOf(stone2.z - stone0.z))
	val v1 = listOf(BigInteger.valueOf(stone1.xVelocity - stone0.xVelocity), BigInteger.valueOf(stone1.yVelocity - stone0.yVelocity), BigInteger.valueOf(stone1.zVelocity - stone0.zVelocity))
	val v2 = listOf(BigInteger.valueOf(stone2.xVelocity - stone0.xVelocity), BigInteger.valueOf(stone2.yVelocity - stone0.yVelocity), BigInteger.valueOf(stone2.zVelocity - stone0.zVelocity))

	var p: List<BigInteger>
	try {
		// t1 = -((p1 x p2) * v2) / ((v1 x p2) * v2)
		val t1 = -(dotProduct(crossProduct(p1, p2), v2)) / dotProduct(crossProduct(v1, p2), v2)
		// t2 = -((p1 x p2) * v1) / ((p1 x v2) * v1)
		val t2 = -(dotProduct(crossProduct(p1, p2), v1)) / dotProduct(crossProduct(p1, v2), v1)
		// c1 = position_1 + t1 * velocity_1
		val c1 = listOf(
			BigInteger.valueOf(stone1.x) + t1 * BigInteger.valueOf(stone1.xVelocity), 
			BigInteger.valueOf(stone1.y) + t1 * BigInteger.valueOf(stone1.yVelocity), 
			BigInteger.valueOf(stone1.z) + t1 * BigInteger.valueOf(stone1.zVelocity)
		)
		// c2 = position_2 + t2 * velocity_2
		val c2 = listOf(
			BigInteger.valueOf(stone2.x) + t2 * BigInteger.valueOf(stone2.xVelocity),
			BigInteger.valueOf(stone2.y) + t2 * BigInteger.valueOf(stone2.yVelocity), 
			BigInteger.valueOf(stone2.z) + t2 * BigInteger.valueOf(stone2.zVelocity)
		)
		// v = (c2 - c1) / (t2 - t1)
		val v = listOf((c2[0] - c1[0]) / (t2 - t1), (c2[1] - c1[1]) / (t2 - t1), (c2[2] - c1[2]) / (t2 - t1))
		// p = c1 - t1 * v 
		p = listOf(c1[0] - (t1 * v[0]), c1[1] - (t1 * v[1]), c1[2] - (t1 *v[2]))
	}
	catch (e: ArithmeticException) {
		return BigInteger.ZERO
	}
	return p[0] + p[1] + p[2]
}

private fun crossProduct(a: List<BigInteger>, b: List<BigInteger>): List<BigInteger> {
	return listOf((a[1]) * b[2] - a[2] * b[1], a[2] * b[0] - a[0] * b[2], a[0] * b[1] - a[1] * b[0])
}

private fun dotProduct(a: List<BigInteger>, b: List<BigInteger>): BigInteger {
	return (a[0] * b[0]) + (a[1] * b[1]) + (a[2] * b[2])
}

private class Hailstone(
	val x: Long,
	val y: Long,
	val z: Long,
	val xVelocity: Long,
	val yVelocity: Long,
	val zVelocity: Long,
	var slope: Double = 0.0,
	var intercept: Double = 0.0
) {
	fun calculateSlope() {
		slope = yVelocity.toDouble() / xVelocity.toDouble()
	}

	fun calculateIntercept() {
		intercept = y - (slope * x)
	}

	fun calculateIntersection(other: Hailstone, lowerBound: Long, upperBound: Long): Boolean {
		if (slope == other.slope) {
			return false
		}
 
		val xIntersection: Double = (other.intercept - intercept) / (slope - other.slope)
		val yIntersection: Double = (slope * xIntersection) + intercept
		
		// check if intersection is outside of bounds
		if (
			!(xIntersection in lowerBound.toDouble()..upperBound.toDouble()) ||
			!(yIntersection in lowerBound.toDouble()..upperBound.toDouble())
		) {
			return false
		}
		
		// check if intersection occurs "forwards" in time
		if ((xIntersection - x) * xVelocity < 0) return false
		if ((xIntersection - other.x) * other.xVelocity < 0) return false
		return true
	}
}
