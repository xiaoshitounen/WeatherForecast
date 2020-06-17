package swu.xl.weatherforecast.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;

import net.sourceforge.pinyin4j.PinyinHelper;

import java.util.ArrayList;
import java.util.List;

import swu.xl.weatherforecast.Baen.CityBean;
import swu.xl.weatherforecast.DataCenter.CityDataManager;
import swu.xl.weatherforecast.DataCenter.DataManager;
import swu.xl.weatherforecast.DataCenter.DataUtil;
import swu.xl.weatherforecast.R;
import swu.xl.weatherforecast.SelfView.CityListView;
import swu.xl.weatherforecast.Utils.PinyinUtil;
import swu.xl.xlletterview.Constant;
import swu.xl.xlletterview.XLLetterView;
import swu.xl.xltoolbar.XLToolBar;

public class CitySearchActivity extends AppCompatActivity {

    //全局控件
    private XLLetterView letterView;
    private CityListView cityListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_search);

        //测试数据
        for (CityBean bean : CityDataManager.getDataManager(this).getBeans()) {
            System.out.println(bean);
        }

        //初始化视图
        initView();
    }

    private void initView() {
        //初始化视图
        XLToolBar toolBar = findViewById(R.id.tool_bar);
        final EditText search_edit = findViewById(R.id.search_edit);
        cityListView = findViewById(R.id.list_view);
        letterView = findViewById(R.id.letter_view);

        //ToolBar
        toolBar.getLogo_btn().setText("搜索城市天气");

        //搜索框监听内容改变
        search_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String search_text = search_edit.getText().toString();

                //判断搜索框是否有内容
                if (TextUtils.isEmpty(search_text)){
                    //1.加载所有内容
                    List<CityBean> cityBeans = DataUtil.loadCityDateByFile(CitySearchActivity.this);
                    List<CityBean> beans = CityDataManager.getDataManager(CitySearchActivity.this).getBeans();
                    beans.clear();
                    beans.addAll(cityBeans);

                    //2.刷新
                    cityListView.getAdapter().notifyDataSetChanged();

                    //3.显示字母侧边栏
                    letterView.setVisibility(View.VISIBLE);
                }else {
                    //1.获取所有的数据
                    List<CityBean> cityBeans = DataUtil.loadCityDateByFile(CitySearchActivity.this);

                    //2.加载指定的数据项
                    List<CityBean> temp = new ArrayList<>();
                    for (CityBean cityBean : cityBeans) {
                        if (cityBean.getName().contains(search_text)){
                            temp.add(cityBean);
                        }
                    }
                    List<CityBean> beans = CityDataManager.getDataManager(CitySearchActivity.this).getBeans();
                    beans.clear();
                    beans.addAll(temp);

                    //3.刷新
                    cityListView.getAdapter().notifyDataSetChanged();

                    //4.隐藏字母侧边栏
                    letterView.setVisibility(View.INVISIBLE);
                }
            }
        });

        //字母侧边监听字母改变
        letterView.setLetterChangeListener(new XLLetterView.LetterChangeListener() {
            @Override
            public void currentLetter(String letter) {
                Log.d(Constant.TAG,"当前的字母："+letter);

                //获取当前字母的城市排在第几个
                int position = 0;
                List<CityBean> beans = CityDataManager.getDataManager(CitySearchActivity.this).getBeans();
                for (int i = 0; i < beans.size(); i++) {
                    String upperCase = beans.get(i).getPinyin().toUpperCase();
                    String city_letter = upperCase.substring(0, 1);

                    if (TextUtils.equals(city_letter,letter)){
                        break;
                    }

                    position++;
                }

                //滑动
                cityListView.setSelection(position);
            }
        });

        //ListView监听item点击
        cityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //获得当前拼音的首字母
                String pinyin = CityDataManager.getDataManager(CitySearchActivity.this).getBeans().get(position).getPinyin();
                String upperCase = pinyin.toUpperCase();
                String letter = upperCase.substring(0, 1);
                Log.d(Constant.TAG,"该地区的首字母："+letter);

                //选中某一个城市
                String name = CityDataManager.getDataManager(CitySearchActivity.this).getBeans().get(position).getName();

                //回调
                Intent intent = new Intent();
                intent.putExtra("city",name);
                setResult(RESULT_OK,intent);
                finish();

                //清空数据
                search_edit.setText("");
            }
        });

        //ListView监听滑动
        cityListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //获取数据集合
                List<CityBean> beans = CityDataManager.getDataManager(CitySearchActivity.this).getBeans();

                if (beans != null & beans.size() != 0){
                    //获得当前拼音的首字母
                    String pinyin = beans.get(firstVisibleItem).getPinyin();
                    String upperCase = pinyin.toUpperCase();
                    String letter = upperCase.substring(0, 1);
                    Log.d(Constant.TAG,"该地区的首字母："+letter);

                    //设置字母侧边栏状态
                    letterView.setCurrentLetter(letter);
                }
            }
        });
    }
}