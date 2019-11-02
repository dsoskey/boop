package wav.boop.visualisation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.androidplot.xy.*
import wav.boop.R

// TODO: Split this out to couple plot to Synthesizer
class OscilloscopeFragment: Fragment() {
    private lateinit var fragmentView: View
    private lateinit var plot: XYPlot

    fun clearPlot() {
        plot.clear()
        plot.redraw()
    }
    fun setPlot(waveform: DoubleArray) {
        plot.clear()
        val series = NormedXYSeries(
            SimpleXYSeries(waveform.toList() + waveform.toList(), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Waveform"),
            NormedXYSeries.Norm(null, 0.0, false),
            NormedXYSeries.Norm(null, 0.0, false)
        )
        val seriesFormat = LineAndPointFormatter(context, R.xml.oscilloscope_line)
        seriesFormat.interpolationParams =
            CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal)
        plot.addSeries(series, seriesFormat)
        plot.redraw()
    }
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