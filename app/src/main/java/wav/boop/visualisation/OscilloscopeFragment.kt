package wav.boop.visualisation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.androidplot.xy.BoundaryMode
import com.androidplot.xy.XYPlot
import wav.boop.R

class OscilloscopeFragment(val oscilloscope: Oscilloscope): Fragment() {
    private lateinit var fragmentView: View
    private lateinit var plot: XYPlot

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, saveInstanceState: Bundle?): View? {
        fragmentView = inflater.inflate(R.layout.oscilloscope, container, false)
        return fragmentView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        plot = fragmentView.findViewById(R.id.plot)
        plot.legend.isVisible = false
        plot.domainTitle.isVisible = false
        plot.rangeTitle.isVisible = false
        plot.title.isVisible = false
        plot.setRangeBoundaries(-0.01, 1.01, BoundaryMode.FIXED)
        plot.setDomainBoundaries(-0.01, 1.01, BoundaryMode.FIXED)
        plot.graph.setMargins(0f, 0f, 0f, 0f)
    }
}