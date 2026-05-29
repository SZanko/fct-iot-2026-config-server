package pt.nova.fct.iot.configserver.models

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "buzzer_stage")
class BuzzerStageModel(
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.UUID)
    var id: String? = null,

    @field:Column(nullable = false)
    var busStopId: String,

    @field:Column(nullable = false)
    var minutesBefore: Int,  // 0 = at arrival

    @field:Column(nullable = false)
    var buzzerType: String = "single",

    @field:Column(nullable = false)
    var buzzerDurationMs: Int = 200,
)
