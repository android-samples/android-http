package com.example.httpsample;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.os.AsyncTask;
import android.os.Bundle;
import android.R.anim;
import android.R.integer;
import android.app.Activity;
import android.text.Html;
import android.text.Spanned;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	ArrayList<String> m_list = new ArrayList<String>();
	ArrayAdapter<String> m_adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// アニメーション設定
		Animation a = AnimationUtils.loadAnimation(this, R.anim.rotate);
		View button3 = findViewById(R.id.button3);
		button3.startAnimation(a);
		
		// リストビュー
		m_list.add("AAA");
		m_list.add("BBB");
		m_list.add("CCC");
		m_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, m_list);
		ListView listView = (ListView)findViewById(R.id.listView1);
		listView.setAdapter(m_adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void buttonMethod(View button){
		try{
			String url = "http://yahoo.co.jp/";
			HttpGet request = new HttpGet(url);
			DefaultHttpClient client = new DefaultHttpClient();
			request.setHeader("Connection", "Keep-Alive");
			HttpResponse response = client.execute(request);
			int status = response.getStatusLine().getStatusCode();
			if(status != HttpStatus.SC_OK){
				throw new Exception("NOT OK");
			}
			// 結果
			String body = EntityUtils.toString(response.getEntity(), "UTF-8");
			//response.getEntity().getContent().read(bytes);
			//response.getEntity().getContentLength();
			//BitmapUtil.decode～
			showResult(url, body);
		}
		catch(Exception ex){
			Toast.makeText(this, ex.toString(), Toast.LENGTH_SHORT).show();
		}
	}
	private void showResult(String baseUrl_, String result){
		m_list.clear();
		/*
		String[] lines = result.split("\n");
		for(int i = 0; i < lines.length; i++){
			if(i > 10)break;
			m_list.add(lines[i]);
		}
		*/
		// パース
		try{
			if(result == null){
				Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
				return;
			}
			URL baseUrl = new URL(baseUrl_);
			Document doc = Jsoup.parse(result);
			Elements links = doc.select("a");
			for(int i = 0; i < links.size(); i++){
				try{
					Element link = links.get(i);
					URL url = new URL(baseUrl, link.attr("href"));
					// フルURL取得
					m_list.add(url.toString());
				}
				catch(MalformedURLException ex){
				}
			}
		}
		catch(MalformedURLException ex){
			
		}
		// 通知
		m_adapter.notifyDataSetChanged();
		
	}

	HttpTask m_task;
	public void buttonMethod2(View button){
		// タスク生成
		if(m_task != null)return;
		m_task = new HttpTask();
		m_task.execute("http://google.co.jp/");
	}
	
	public void buttonMethod3(View button){
		showResult("", "");
	}

	// リンク
	public void buttonMethod4(View button){
		showResult("", "");
	}

	// 通信タスク
	class HttpTask extends AsyncTask<String, Integer, String>{
		String m_url = "";
		
		// オプション：事前準備
		@Override
		protected void onPreExecute() {
			// ローディング表示
			findViewById(R.id.progressBar1).setVisibility(View.VISIBLE);
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		// 必須：バッググラウンド処理を書く
		// AsyncTask の cancel を呼び出す。すると、doInBackground は InterruptedException がおきて終了して、その後 onPostExecute ではなく onCancelled が呼ばれました。
		@Override
		protected String doInBackground(String... params) {
			if(params.length == 0)return null;
			String result = null;
			DefaultHttpClient client = new DefaultHttpClient();
			try{
				m_url = params[0];
				HttpGet request = new HttpGet(m_url);
				HttpResponse response = client.execute(request);
				if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
					result = EntityUtils.toString(response.getEntity());
				}
			}
			catch(Exception ex){
			}
			return result;
		}

		// オプション：進捗状況をUIスレッドで表示する処理
		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}
		
		// オプション：事後処理（バックグラウンド処理が完了し、UIスレッドに反映する処理を書く）（キャンセル時は呼ばれない）
		@Override
		protected void onPostExecute(String result) {
			// 結果表示
			showResult(m_url, result);
			m_task = null;
			// ローディング非表示
			findViewById(R.id.progressBar1).setVisibility(View.INVISIBLE);

			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}

		// オプション：キャンセルした状態（cancelが呼ばれた状態）でdoInBackgroundを抜けたときに呼ばれる
		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
		}

	}
}
