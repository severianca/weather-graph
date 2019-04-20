package com.example.maria.weathergraph;

import android.graphics.Color;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class Main extends AppCompatActivity {

    public String dataStringMas[];
    public int dataIntMas[];
    public String dataDate[];

    public Elements content, temp, day;

    LineGraphSeries<DataPoint> seriesDay;
    LineGraphSeries<DataPoint> seriesNight;
    GraphView graph;
    StaticLabelsFormatter staticLabelsFormatter;

    public ArrayList<String> titleList = new ArrayList<String>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataStringMas = new String[12];
        dataIntMas = new int[12];
        dataDate = new String[6];

        new NewThread().execute();

        graph = (GraphView) findViewById(R.id.graph);

        //createGraph();
    }

    public class NewThread extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            Document doc;
            try {
                doc = Jsoup.connect("https://www.meteoservice.ru/weather/overview/ufa").get();
                content = doc.select("div [class=row forecast-week-overview small-up-6 medium-up-6 large-up-6 margin-bottom-0]");
                //получаем температуру (день и ночь)
                temp = content.select(".value");
                //получаем день
                day = content.select("div [class=text-nowrap grey font-smaller margin-bottom-1]");
                //подготавливаем данные для работы с графиком
                preparationData();
            } catch (IOException e){
                e.printStackTrace();
            }
            return null;
        }
    }

    public void preparationData(){
        //разделяем полученные данные температруты (для графика в дальнейшем проигодиться)
        for (int i =0; i<12; i++){
            dataStringMas[i]=temp.get(i).text();
        }
        //разделяем полученные даты
        for (int i=0;i<6; i++){
            dataDate[i]=day.get(i).text();
        }
        //удаляем значок температуры (для дальнейшего преобразования string в int
        for(int i =0;i<12;i++){
            dataStringMas[i] = dataStringMas[i].substring(0,dataStringMas[i].length()-1);
        }
        //преобразуем все строковые данные в числовые
        for (int i=0;i<12;i++){
            dataIntMas[i]= Integer.parseInt(dataStringMas[i]);
        }
        //Ура! данные готовы для выведения в график
        //выводим график
        createGraph();

    }

    public void createGraph() {

        seriesDay = new LineGraphSeries<DataPoint>(new DataPoint[]{
                new DataPoint(0,dataIntMas[0]),
                new DataPoint(2,dataIntMas[2]),
                new DataPoint(4,dataIntMas[4]),
                new DataPoint(6,dataIntMas[6]),
                new DataPoint(8,dataIntMas[8]),
                new DataPoint(10,dataIntMas[10])
        });
        seriesNight = new LineGraphSeries<DataPoint>(new DataPoint[]{
                new DataPoint(0,dataIntMas[1]),
                new DataPoint(2,dataIntMas[3]),
                new DataPoint(4,dataIntMas[5]),
                new DataPoint(6,dataIntMas[7]),
                new DataPoint(8,dataIntMas[9]),
                new DataPoint(10,dataIntMas[11])
        });
        graph.addSeries(seriesDay);
        graph.addSeries(seriesNight);
        graph.setTitleTextSize(60);

        seriesDay.setDrawDataPoints(true);
        seriesNight.setDrawDataPoints(true);
        seriesDay.setColor(Color.RED);
        seriesNight.setColor(Color.GREEN);
        seriesDay.setDataPointsRadius(10);
        seriesNight.setDataPointsRadius(10);
        seriesDay.setThickness(8);
        seriesNight.setThickness(8);

        staticLabelsFormatter = new StaticLabelsFormatter(graph);

        staticLabelsFormatter.setHorizontalLabels(new String[] {dataDate[0], dataDate[1], dataDate[2], dataDate[3], dataDate[4], dataDate[5]});
        staticLabelsFormatter.setVerticalLabels(new String[] {" ","5","10","15","20"});
        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

        GridLabelRenderer renderer = graph.getGridLabelRenderer();
        renderer.setHorizontalLabelsAngle(90);
    }
}
