package com.example.cooldrop

import java.util.UUID

data class User (val name: String, var publicUuid: UUID, val privateUuid: UUID)