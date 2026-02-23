package com.bumi.app.data.mapper

import com.bumi.app.data.response.BookingDto
import com.bumi.app.domain.model.Meeting

fun BookingDto.toDomain(): Meeting {
    return Meeting(
        id = this.id,
        pic = this.pic_name,
        unit = this.unit_name,
        tujuan = this.meeting_purpose,
        tanggal = this.booking_date,
        // Kita petakan hasil GROUP_CONCAT dari PHP ke property jam
        jam = this.jam_dipilih,
        status = this.status
    )
}