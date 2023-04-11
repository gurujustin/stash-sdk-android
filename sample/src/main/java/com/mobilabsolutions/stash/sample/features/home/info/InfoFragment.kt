package com.mobilabsolutions.stash.sample.features.home.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.mobilabsolutions.stash.sample.data.SamplePreference
import com.mobilabsolutions.stash.sample.databinding.FragmentInfoBinding
import com.mobilabsolutions.stash.sample.shared.BaseFragment
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 26-08-2019.
 */
class InfoFragment : BaseFragment() {
    @Inject
    lateinit var infoViewModelFactory: InfoViewModel.Factory
    @Inject
    lateinit var infoTextCreator: InfoTextCreator

    private val viewModel: InfoViewModel by fragmentViewModel()
    private lateinit var binding: FragmentInfoBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentInfoBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.textCreator = infoTextCreator
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ccSpinner.adapter = ArrayAdapter<SamplePreference.Psp>(view.context, android.R.layout.simple_list_item_1, SamplePreference.Psp.values())
        binding.ccSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                // do nothing
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                viewModel.onCcPspSelected(position)
            }
        }

        binding.sepaSpinner.adapter = ArrayAdapter<SamplePreference.Psp>(view.context, android.R.layout.simple_list_item_1, SamplePreference.Psp.values())
        binding.sepaSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                // do nothing
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                viewModel.onSepaPspSelected(position)
            }
        }
    }

    override fun invalidate() {
        withState(viewModel) { state ->
            binding.ccSpinner.isEnabled = !state.stashInitialized
            binding.sepaSpinner.isEnabled = !state.stashInitialized

            state.creditCardPref?.let {
                binding.ccSpinner.setSelection(it.ordinal)
            }
            state.sepaPref?.let {
                binding.sepaSpinner.setSelection(it.ordinal)
            }
        }
    }
}