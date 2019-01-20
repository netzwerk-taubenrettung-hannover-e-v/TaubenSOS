package de.unihannover.se.tauben2.model.database

import androidx.annotation.StringRes
import de.unihannover.se.tauben2.App
import de.unihannover.se.tauben2.R

enum class PigeonBreed(val backendTitle: String?, @StringRes val titleResource: Int) {

    CARRIER("Carrier Pigeon", R.string.carrier_pigeon), COMMON_WOOD("Common Wood Pigeon", R.string.common_wood_pigeon),
    FERAL("Feral Pigeon", R.string.feral_pigeon), FANCY("Fancy Pigeon", R.string.fancy_pigeon), NO_SPECIFICATION(null, R.string.no_specification);

    companion object {
        fun fromString(title: String?): PigeonBreed {
            for(breed in PigeonBreed.values())
                if (breed.backendTitle == title)
                    return breed
            return PigeonBreed.NO_SPECIFICATION
        }

        fun fromPigeonBreed(pigeonBreed: PigeonBreed) = pigeonBreed.backendTitle
    }

    fun getTitle() = App.context.getString(titleResource)
}