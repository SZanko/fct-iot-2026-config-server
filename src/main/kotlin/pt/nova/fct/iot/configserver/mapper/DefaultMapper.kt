package pt.nova.fct.iot.configserver.mapper

interface DefaultMapper<D, M> {
    fun toModel(data: D): M
    fun toModel(data: List<D>): List<M> {
        return data.map { toModel(it) }
    }

    fun toDto(data: M): D
    fun toDto(data: List<M>): List<D> {
        return data.map { toDto(it) }
    }
}