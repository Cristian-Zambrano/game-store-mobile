package com.example.game_store_mobile.modelo

import android.os.Parcel
import android.os.Parcelable

class CatalogoVideojuego (
    var id: Int = 0,
    var nombre: String,
    var maximoNumeroDeJuegos: Int

) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(nombre)
        parcel.writeInt(maximoNumeroDeJuegos)

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CatalogoVideojuego> {
        override fun createFromParcel(parcel: Parcel): CatalogoVideojuego {
            return CatalogoVideojuego(parcel)
        }

        override fun newArray(size: Int): Array<CatalogoVideojuego?> {
            return arrayOfNulls(size)
        }
    }
}