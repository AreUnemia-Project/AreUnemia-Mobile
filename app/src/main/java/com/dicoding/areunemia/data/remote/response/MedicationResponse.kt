package com.dicoding.areunemia.data.remote.response

data class MedicationResponse(
	val medications: List<MedicationsItem>? = null,
	val message: String? = null,
	val status: String
)

data class MedicationsItem(
	val medicationName: String,
	val dosage: String,
	val schedule: List<String>,
	val createdAt: CreatedAt,
	val notes: String,
	val endDate: String,
	val id: String,
	val startDate: String
)

data class CreatedAt(
	val nanoseconds: Int,
	val seconds: Int
)

data class MedicationData(
	val medicationName: String,
	val dosage: String,
	val schedule: String,
	val createdAt: CreatedAt,
	val notes: String,
	val endDate: String,
	val id: String,
	val startDate: String
)

