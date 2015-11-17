// demonstrates the reading of XML resource files using 
// a SAX XmlPullParser
//Exam 2 Mobile Devices Aidan O'Brien G00289968
// ---------------------------------------------------------------------
package ucr.xmlreading;

import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Environment;
import android.widget.Toast;
import org.xmlpull.v1.XmlPullParser;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class ActivityMain extends Activity {

	private String SdPath;
	private TextView txtMsg;
	Button btnGoParser;
	Button btnSD;
	SQLiteDatabase db;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		txtMsg = (TextView) findViewById(R.id.txtMsg);

		btnGoParser = (Button) findViewById(R.id.btnReadXml);

		btnGoParser.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				btnGoParser.setEnabled(false);
				Integer xmlResFile = R.xml.employee;
				new backgroundAsyncTask().execute(xmlResFile);
			}
		});

		SdPath = Environment.getExternalStorageDirectory().getAbsolutePath();

		btnSD = (Button) findViewById(R.id.btnSD);
		btnSD.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					File myFile = new File(SdPath + "/stuff.db");

					OutputStreamWriter myOutWriter = new OutputStreamWriter(
							new FileOutputStream(myFile));

					myOutWriter.append(txtMsg.getText());
					myOutWriter.close();

					Toast.makeText(getBaseContext(),
							"Done writing SD 'stuff.db' to" + SdPath,
							Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					Toast.makeText(getBaseContext(), e.getMessage(),
							Toast.LENGTH_SHORT).show();
				}
				// WRITE on SD card file data taken from the text box
				try {
					openDatabase(); // open (create if needed) database
					dropTable(); // if needed drop table tblAmigos
					insertSomeDbData();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}// onClick



		}); // btnWriteSDFile

//		dbBtn = (Button) findViewById(R.id.buttondb);
//		dbBtn.setOnClickListener(new View.OnClickListener(){
//			@Override
//			public void onClick(View v) {
//				Intent dbIntent = new Intent (ActivityMain.this,
//						Activity2.class);
//				// create a Bundle (MAP) container to ship data
//				// call Activity1, tell your local listener to wait a
//				// response sent to a listener known as 101
//				startActivityForResult(dbIntent, 101);
//			}
//		});

//		openDatabase();
//		dropTable(); // if needed drop table tblAmigos
//		insertSomeDbData();

	}// onCreate



	private void dropTable() {
			// (clean start) action query to drop table

			try {
				db.execSQL("DROP TABLE IF EXISTS tblExam;");
				txtMsg.append("\n-dropTable - dropped!!");
			} catch (Exception e) {
				txtMsg.append("\nError dropTable: " + e.getMessage());
				finish();
			}
		}
	private void openDatabase() {
		try {
			// path to private memory:
			//String SdPath = "data/data/cis470.matos.databases";
			// -----------------------------------------------------------
			// this provides the path name to the SD card
			// String SDcardPath = Environment.getExternalStorageDirectory().getPath();

			String DbPath = SdPath + "/" + "myStuffDB2.db";
			txtMsg.append("\n-openDatabase - DB Path: " + DbPath);

			db = SQLiteDatabase.openDatabase(DbPath, null,
					SQLiteDatabase.CREATE_IF_NECESSARY);

			txtMsg.append("\n-openDatabase - DB was opened");
		} catch (SQLiteException e) {
			txtMsg.append("\nError openDatabase: " + e.getMessage());
			finish();
		}
	}
	private void insertSomeDbData() {
		// create table: tblEmployee
		db.beginTransaction();
		try {
			// create table
			db.execSQL("create table tblEmployees ("
					+ " recID integer PRIMARY KEY autoincrement, "
					+ " FirstName  text, "+ " LastName  text, "+ " DOB  text, "+ " Address  text, "+ " Sex  text, "+ " Salary  text, "+ " SuperSsn  text, "+ " DNO  text); ");
			// commit your changes
			db.setTransactionSuccessful();

			txtMsg.append("\n-insertSomeDbData - Table was created");

		} catch (SQLException e1) {
			txtMsg.append("\nError insertSomeDbData: " + e1.getMessage());
			finish();
		} finally {
			db.endTransaction();
		}

		// populate table: tblAmigo
		db.beginTransaction();
		try {

			// insert rows
			db.execSQL("insert into tblEmployees(FirstName, LastName, DOB, Address, Sex, Salary, SuperSsn, DNO) "
					+ " values ('JohnB', 'Smith', '123456789', '1955-01-09T00:00:00', '731FondrenHoustonTX', 'M', '30000', '987654321', '5' );");
					//+ " values ('BBB', '555-2222' );");
			db.execSQL("insert into tblEmployees(FirstName, LastName, DOB, Address, Sex, Salary, SuperSsn, DNO) "
			//db.execSQL("insert into tblEmployees(name, phone) "
					+ " values ('BBB', '555-2222' );");
			db.execSQL("insert into tblEmployees(FirstName, LastName, DOB, Address, Sex, Salary, SuperSsn, DNO) "
			//db.execSQL("insert into tblEmployees(name, phone) "
					+ " values ('CCC', '555-3333' );");

			// commit your changes
			db.setTransactionSuccessful();
			txtMsg.append("\n-insertSomeDbData - 3 rec. were inserted");

		} catch (SQLiteException e2) {
			txtMsg.append("\nError insertSomeDbData: " + e2.getMessage());

		} finally {
			db.endTransaction();
		}

	}// insertSomeData


	// ///////////////////////////////////////////////////////////////////

	public class backgroundAsyncTask extends
			AsyncTask<Integer, Void, StringBuilder> {
		
		ProgressDialog dialog = new ProgressDialog(ActivityMain.this);



		@Override
		protected void onPostExecute(StringBuilder result) {
			super.onPostExecute(result);
			dialog.dismiss();

			txtMsg.setText(result.toString());
		}

						// open (create if needed) database
//		dropTable(); 					// if needed drop table tblAmigos
//		insertSomeDbData(); 			// create-populate tblAmigos


		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog.setMessage("Please wait...");
			dialog.setCancelable(false);
			dialog.show();

		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}

		@Override
		protected StringBuilder doInBackground(Integer... params) {
			int xmlResFile = params[0];
			XmlPullParser parser = getResources().getXml(xmlResFile);
					
			StringBuilder stringBuilder = new StringBuilder();
			String nodeText = "";
			String nodeName = "";
			try {
				int eventType = -1;
				while (eventType != XmlPullParser.END_DOCUMENT) {

					eventType = parser.next();

					if (eventType == XmlPullParser.START_DOCUMENT) {
						stringBuilder.append("\nSTART_DOCUMENT");

					} else if (eventType == XmlPullParser.END_DOCUMENT) {
						stringBuilder.append("\nEND_DOCUMENT");

					} else if (eventType == XmlPullParser.START_TAG) {
						nodeName = parser.getName();
						stringBuilder.append("\nSTART_TAG: " + nodeName);

						stringBuilder.append(getAttributes(parser));

					} else if (eventType == XmlPullParser.END_TAG) {
						nodeName = parser.getName();
						stringBuilder.append("\nEND_TAG:   " + nodeName );

					} else if (eventType == XmlPullParser.TEXT) {
						nodeText = parser.getText();
						stringBuilder.append("\n    TEXT: " + nodeText);

					}
				}
			} catch (Exception e) {
				Log.e("<<PARSING ERROR>>", e.getMessage());

			}

			return stringBuilder;
		}// doInBackground

		private String getAttributes(XmlPullParser parser) {
			StringBuilder stringBuilder = new StringBuilder();
			// trying to detect inner attributes nested inside a node tag
			String name = parser.getName();
			if (name != null) {
				int size = parser.getAttributeCount();

				for (int i = 0; i < size; i++) {
					String attrName = parser.getAttributeName(i);
					String attrValue = parser.getAttributeValue(i);
					stringBuilder.append("\n    Attrib <key,value>= "
							+ attrName + ", " + attrValue);
				}

			}
			return stringBuilder.toString();

		}


		// innerElements

	}// backroundAsyncTask

}// ActivityMain