package com.beautyhealthapp.SafeGuardianship.Assistant;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;

import com.infrastructure.CWAssistant.CWChartView;

import org.xclcharts.chart.PointD;
import org.xclcharts.chart.SplineChart;
import org.xclcharts.chart.SplineData;
import org.xclcharts.common.IFormatterTextCallBack;
import org.xclcharts.event.click.PointPosition;
import org.xclcharts.renderer.XChart;
import org.xclcharts.renderer.XEnum;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class SafeActionView extends CWChartView {
	private Context cxt;
	private List<SafeActionLine> Lines = null;
	private String SubChartTitel = "测量时间";

	private String TAG = "SplineChartView";
	private SplineChart chart = new SplineChart();
	// 分类轴标签集合
	private LinkedList<SplineData> chartData = new LinkedList<SplineData>();
	private Paint mPaintTooltips = new Paint(Paint.ANTI_ALIAS_FLAG);

	private List<Integer> colors = new ArrayList<Integer>();

	public String getSubChartTitel() {
		return SubChartTitel;
	}

	public void setSubChartTitel(String subChartTitel) {
		SubChartTitel = subChartTitel;
	}

	public SplineChart getChart() {
		return chart;
	}

	public void setChart(SplineChart chart) {
		this.chart = chart;
	}

	public List<Integer> getColors() {
		return colors;
	}

	public void setColors(List<Integer> colors) {
		this.colors = colors;
	}

	public SafeActionView(Context context) {
		super(context);
		cxt = context;
		initView();
	}

	public SafeActionView(Context context, List<SafeActionLine> _lines,
			List<Integer> _colors) {
		super(context);
		cxt = context;
		Lines = _lines;
		colors = _colors;
		initView();
	}

	public SafeActionView(Context context, AttributeSet attrs) {
		super(context, attrs);
		cxt = context;
		initView();
	}

	public SafeActionView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		cxt = context;
		initView();
	}

	private void initView() {
		setSubTitle();
		setChartDataSet();
		chartRender();
	}

	private void setSubTitle() {
		if (Lines != null && Lines.size() > 0) {
			SafeActionLine achartLine2d = Lines.get(0);
			List<SafeActionPoint> chartPoints = achartLine2d.SafeActionPoint;
			if (chartPoints.size() > 0) {
				SafeActionPoint achartPoint2d_Start = chartPoints.get(0);
				SafeActionPoint achartPoint2d_End = chartPoints.get(chartPoints
						.size() - 1);
				String[] times_Start = achartPoint2d_Start.AXLabel.split(" ");
				String[] times_End = achartPoint2d_End.AXLabel.split(" ");
				SubChartTitel = SubChartTitel + ":"
						+ times_Start[0] + "——"
						+ times_End[0];
			}
		}

	}

	public void setChartDataSet() {
		if (Lines != null && Lines.size() > 0) {
			for (int i = 0; i < Lines.size(); i++) {
				SafeActionLine achartLine2d = Lines.get(i);

				List<SafeActionPoint> chartPoints = achartLine2d.SafeActionPoint;
				if (chartPoints.size() > 0) {
					List<PointD> linePoint = new ArrayList<PointD>();
					for (int j = 0; j < chartPoints.size(); j++) {
						SafeActionPoint achartPoint2d = chartPoints.get(j);
						PointD ap = new PointD(achartPoint2d.AX,
								achartPoint2d.AY);
						linePoint.add(ap);
					}
					SplineData dataSeries;
					if (colors.size() > 0) {
						dataSeries = new SplineData(achartLine2d.LineTitle,
								linePoint, colors.get(i));
					} else {
						dataSeries = new SplineData(achartLine2d.LineTitle,
								linePoint, Color.rgb(54, 141, 238));
					}
					// 把线弄细点
					dataSeries.getLinePaint().setStrokeWidth(3);
					dataSeries.setLabelVisible(false);
					// 设定数据源
					chartData.add(dataSeries);
				}
			}
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		// 图所占范围大小
		chart.setChartRange(w, h);
	}

	private void chartRender() {
		try {

			// 设置绘图区默认缩进px值,留置空间显示Axis,Axistitle....
			int[] ltrb = getBarLnDefaultSpadding();
			chart.setPadding(ltrb[0], ltrb[1], ltrb[2], ltrb[3]);

			// 显示边框
			chart.showRoundBorder();

			// 数据源
			chart.setDataSource(chartData);

			// 坐标系-Y轴部分在调用处设置

			// 标签轴最大值
			chart.setCategoryAxisMax(100);
			// 标签轴最小值
			chart.setCategoryAxisMin(0);

			// 设置图的背景色
			chart.setApplyBackgroundColor(true);
			chart.setBackgroundColor(Color.rgb(255, 255, 255));
			chart.getBorder().setBorderLineColor(Color.rgb(179, 147, 197));

			// 调轴线与网络线风格
			chart.getCategoryAxis().hideTickMarks();
			chart.getDataAxis().hideAxisLine();
			chart.getDataAxis().hideTickMarks();
			chart.getPlotGrid().showHorizontalLines();
			// chart.hideTopAxis();
			// chart.hideRightAxis();

			chart.getCategoryAxis()
					.getAxisPaint()
					.setColor(
							chart.getPlotGrid().getHorizontalLinePaint()
									.getColor());
			chart.getCategoryAxis()
					.getAxisPaint()
					.setStrokeWidth(
							chart.getPlotGrid().getHorizontalLinePaint()
									.getStrokeWidth());

			// 定义交叉点标签显示格式,特别备注,因曲线图的特殊性，所以返回格式为: x值,y值
			// 请自行分析定制--是什么 作用还不知道
			chart.setDotLabelFormatter(new IFormatterTextCallBack() {

				@Override
				public String textFormatter(String value) {
					// TODO Auto-generated method stub
					String label = "[" + value + "]";
					return (label);
				}

			});

			// 子标题
			chart.addSubtitle(SubChartTitel);

			// 激活点击监听
			chart.ActiveListenItemClick();
			// 为了让触发更灵敏，可以扩大15px的点击监听范围
			chart.extPointClickRange(20);
			chart.showClikedFocus();

			// 禁止缩放
			chart.disableScale();
			// 仅横向平移
			chart.setPlotPanMode(XEnum.PanMode.HORIZONTAL);

			// 显示平滑曲线
			chart.setCrurveLineStyle(XEnum.CrurveLineStyle.BEZIERCURVE);

			// 图例显示在正下方
			chart.getPlotLegend().setVerticalAlign(XEnum.VerticalAlign.BOTTOM);
			chart.getPlotLegend().setHorizontalAlign(
					XEnum.HorizontalAlign.CENTER);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG, e.toString());
		}
	}

	@Override
	public void render(Canvas canvas) {
		try {
			chart.render(canvas);
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
	}

	@Override
	public List<XChart> bindChart() {
		// TODO Auto-generated method stub
		List<XChart> lst = new ArrayList<XChart>();
		lst.add(chart);
		return lst;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub

		if (event.getAction() == MotionEvent.ACTION_UP) {
			triggerClick(event.getX(), event.getY());
		}
		super.onTouchEvent(event);
		return true;
	}

	// 触发监听
	private void triggerClick(float x, float y) {
		if (!chart.getListenItemClickStatus()) {
			this.invalidate();
			return;
		} else {
			PointPosition record = chart.getPositionRecord(x, y);
			if (null == record) {
				this.invalidate();
				return;
			}
			int LinePosition = record.getDataID();
			if (LinePosition >= chartData.size())
				return;

			SplineData lData = chartData.get(LinePosition);

			List<PointD> linePoint = lData.getLineDataSet();
			int pos = record.getDataChildID();
			int i = 0;
			Iterator it = linePoint.iterator();
			while (it.hasNext()) {
				PointD entry = (PointD) it.next();

				if (pos == i) {
					Double xValue = entry.x;
					Double yValue = entry.y;

					float r = record.getRadius();
					chart.showFocusPointF(record.getPosition(), 15);
					chart.getFocusPaint().setStyle(Style.FILL);
					chart.getFocusPaint().setStrokeWidth(3);
					chart.getFocusPaint().setColor(Color.RED);

					// 在点击处显示tooltip
					mPaintTooltips.setColor(Color.RED);
					mPaintTooltips.setTextSize(30);

					chart.getToolTip().addToolTip(lData.getLineKey(),
							mPaintTooltips);

					SafeActionLine achartLine2d = Lines.get(LinePosition);

					chart.getToolTip().addToolTip(
							"  当前值:" + (int) Math.floor(yValue) + " "
									+ achartLine2d.YUnit, mPaintTooltips);
					// pos
					List<SafeActionPoint> chartPoints = achartLine2d.SafeActionPoint;
					SafeActionPoint achartPoint2d = chartPoints.get(pos);
					PointD ap = new PointD(achartPoint2d.AX, achartPoint2d.AY);

					String[] times = achartPoint2d.AXLabel.split(" ");

					String tempDate = "  日期:" + times[0];
					chart.getToolTip().addToolTip(tempDate, mPaintTooltips);
					chart.getToolTip().addToolTip(
							"  时长:" + " " + achartPoint2d.TimeSpan,
							mPaintTooltips);
					chart.getToolTip().getBackgroundPaint().setAlpha(100);

					// 如果超过了右边，则在右边显示
					DisplayMetrics dm = getResources().getDisplayMetrics();
					int scrWidth = (int) (dm.widthPixels);
					int tooltipWidth = tempDate.length() * 22;
					if (tooltipWidth + x > scrWidth) {
						chart.getToolTip().setCurrentXY(x - tooltipWidth, y);
					} else {
						chart.getToolTip().setCurrentXY(x, y);
					}

					this.invalidate();

					break;
				}
				i++;
			}// end while
			this.invalidate();
		}
	}

}
