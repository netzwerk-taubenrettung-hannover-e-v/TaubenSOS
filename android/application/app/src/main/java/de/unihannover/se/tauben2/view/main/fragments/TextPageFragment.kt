package de.unihannover.se.tauben2.view.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.databinding.FragmentTextPageBinding
import de.unihannover.se.tauben2.model.TextPage

abstract class TextPageFragment(@StringRes mTitleRes: Int): BaseMainFragment(mTitleRes) {

    private lateinit var mBinding: FragmentTextPageBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_text_page, container, false)
        setTextPage(getTextPage())
        return mBinding.root
    }

    abstract fun getTextPage(): TextPage

    fun setTextPage(textPage: TextPage) {
        mBinding.textPage = textPage
    }
}