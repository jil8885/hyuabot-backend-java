package app.hyuabot.backend.repository.cafeteria

import app.hyuabot.backend.domain.Campus
import app.hyuabot.backend.domain.cafeteria.Cafeteria
import jakarta.persistence.*
import org.apache.commons.lang3.builder.ToStringExclude
import org.springframework.data.jpa.repository.JpaRepository

interface CafeteriaRepository : JpaRepository<Cafeteria, Int>