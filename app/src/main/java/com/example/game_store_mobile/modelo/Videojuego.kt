package com.example.game_store_mobile.modelo

import android.os.Parcel
import android.os.Parcelable

data class Videojuego (val id: Int,
                       val nombre: String,
                       val tipo: String?,
                       val distancia: Int,
                       val catalogoVideojuegoId: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(nombre)
        parcel.writeString(tipo)
        parcel.writeInt(distancia)
        parcel.writeInt(sistemaSolarId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Videojuego> {
        override fun createFromParcel(parcel: Parcel): Videojuego {
            return Videojuego(parcel)
        }

        override fun newArray(size: Int): Array<Videojuego?> {
            return arrayOfNulls(size)
        }
    }

    override fun toString(): String {
        return "Planeta(id=$id, n:'$nombre', t:$tipo, d:$distancia, ssd:$sistemaSolarId)"

    }

}