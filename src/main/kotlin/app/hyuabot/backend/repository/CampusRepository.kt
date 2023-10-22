package app.hyuabot.backend.repository

import app.hyuabot.backend.domain.Campus
import app.hyuabot.backend.domain.cafeteria.Cafeteria
import app.hyuabot.backend.domain.library.ReadingRoom
import jakarta.persistence.*
import org.apache.commons.lang3.builder.ToStringExclude
import org.springframework.data.jpa.repository.JpaRepository

interface CampusRepository : JpaRepository<Campus, Int>
