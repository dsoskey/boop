package wav.boop.visualisation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.androidplot.ui.HorizontalPositioning
import com.androidplot.ui.Size
import com.androidplot.ui.SizeMode
import com.androidplot.ui.VerticalPositioning
import com.androidplot.xy.*
import wav.boop.R

class OscilloscopeFragment(private val oscilloscope: HistoricalOscilloscope): Fragment() {
    private lateinit var fragmentView: View
    private lateinit var plot: XYPlot

//    fun setPlot() {
//        return { waveform: DoubleArray ->
//
//        }
//        plot.clear()
//        val waveformList = waveform.toList()
//        val series = NormedXYSeries(
//            SimpleXYSeries(
//                waveformList + waveformList + waveformList,
//                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Waveform"
//            ),
//            NormedXYSeries.Norm(null, 0.0, false),
//            NormedXYSeries.Norm(null, 0.0, false)
//        )
//        val seriesFormat = LineAndPointFormatter(context, R.xml.oscilloscope_line)
//        seriesFormat.interpolationParams =
//            CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal)
//        plot.addSeries(series, seriesFormat)
//        plot.redraw()
//    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, saveInstanceState: Bundle?): View? {
        fragmentView = inflater.inflate(R.layout.oscilloscope, container, false)
        return fragmentView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        configurePlotLayout()
//        oscilloscope.onHistoryChanged.add { waveform ->
//            plot.clear()
//            val waveformList = waveform.toList()
//            val series = NormedXYSeries(
//                SimpleXYSeries(
//                    waveformList + waveformList + waveformList,
//                    SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Waveform"
//                ),
//                NormedXYSeries.Norm(null, 0.0, false),
//                NormedXYSeries.Norm(null, 0.0, false)
//            )
//            val seriesFormat = LineAndPointFormatter(context, R.xml.oscilloscope_line)
//            seriesFormat.interpolationParams =
//                CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal)
//            plot.addSeries(series, seriesFormat)
//            plot.redraw()
//        }
    }

    private fun configurePlotLayout() {
        plot = fragmentView.findViewById(R.id.plot)
        plot = fragmentView.findViewById(R.id.plot)
        plot.legend.isVisible = false
        plot.legend.setHeight(0f)
        plot.legend.setWidth(0f)
        plot.legend.setMargins(0f, 0f, 0f, 0f)

        plot.domainTitle.isVisible = false
        plot.domainTitle.setHeight(0f)
        plot.domainTitle.setWidth(0f)
        plot.domainTitle.setMargins(0f, 0f, 0f, 0f)

        plot.rangeTitle.isVisible = false
        plot.rangeTitle.setHeight(0f)
        plot.rangeTitle.setWidth(0f)
        plot.rangeTitle.setMargins(0f, 0f, 0f, 0f)

        plot.title.isVisible = false
        plot.setRangeBoundaries(-0.5, 1.5, BoundaryMode.FIXED)
        plot.setDomainBoundaries(0, 1, BoundaryMode.FIXED)
        plot.graph.size = Size(0f, SizeMode.FILL, 0f, SizeMode.FILL)
        plot.graph.setMargins(0f, 0f, 0f, 0f)
        plot.graph.position(0f, HorizontalPositioning.ABSOLUTE_FROM_LEFT, 0f, VerticalPositioning.ABSOLUTE_FROM_TOP)
    }
}