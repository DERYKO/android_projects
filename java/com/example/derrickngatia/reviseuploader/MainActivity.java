package com.example.derrickngatia.reviseuploader;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {
    String Storage_Path = "All_Uploads/";
    String Database_Path = "All_Image_Uploads_Database";
    private static final int IMAGE_PICK_REQUEST =1234 ;
    EditText ed2,ed3,ed4,ed5,ed6;
    Spinner mspinner;
    EditText ed1;
    ImageView myimage;
    ProgressDialog progress;
    Button submit,choose;
    public Uri filepath;
    private StorageReference mStorageRef;
    FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
    DatabaseReference reference;
    DatabaseReference rootreference=firebaseDatabase.getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ed1 = (EditText) findViewById(R.id.ed1);
        ed2=(EditText)findViewById(R.id.ed2);
        ed3=(EditText)findViewById(R.id.ed3);
        ed4=(EditText)findViewById(R.id.ed4);
        ed5=(EditText)findViewById(R.id.ed5);
        ed6=(EditText)findViewById(R.id.imageName);
        myimage=(ImageView)findViewById(R.id.myimage);
        mspinner= (Spinner) findViewById(R.id.myspinner);
        progress=new ProgressDialog(this);
        progress.setTitle("uploading file");
        submit=(Button)findViewById(R.id.submit);
        choose=(Button)findViewById(R.id.choose);
        //spinner();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog(true,10000);
                if( ed2.getText().toString().isEmpty() | ed3.getText().toString().isEmpty() | ed4.getText().toString().isEmpty() | ed5.getText().toString().isEmpty() ){
                    Toast.makeText(getApplicationContext(),"Empty field found",Toast.LENGTH_LONG).show();
                }else {
                    reference = rootreference.child(ed1.getText().toString());
                    user myuser = new user();
                    myuser.setUnit_name(ed2.getText().toString());
                    myuser.setUnit_code(ed3.getText().toString());
                    myuser.setYear_sem(ed4.getText().toString() + "_" + ed5.getText().toString());
                    reference.push().setValue(myuser);
                    upload();
                }
            }
        });
        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent,"select an image"),IMAGE_PICK_REQUEST);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==IMAGE_PICK_REQUEST && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            filepath=data.getData();
            try {
                Bitmap map= MediaStore.Images.Media.getBitmap(getContentResolver(),filepath);
                myimage.setImageBitmap(map);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    public  void upload(){
        mStorageRef = FirebaseStorage.getInstance().getReference();
        StorageReference riversRef=mStorageRef.child(""+ed3.getText().toString()+"\t"+ed2.getText().toString()+"/"+ed6.getText().toString()+"."+GetFileExtension(filepath));
        riversRef.putFile(filepath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                showProgressDialog(true,10000);
                String currentDateTimeString = DateFormat.getDateInstance().format(new Date());
                Toast.makeText(getApplicationContext(),"file uploaded",Toast.LENGTH_LONG).show();
                String TempImageName=ed6.getText().toString();
                DatabaseReference image_info_reference=rootreference.child(ed1.getText().toString()).child(ed2.getText().toString());
                 upload upload=new upload(TempImageName,taskSnapshot.getDownloadUrl().toString(),currentDateTimeString,"0");
                image_info_reference.push().setValue(upload);
                progress.dismiss();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double mprogress=(100.0 *taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                progress.setMessage((int) mprogress+"%uploaded...");
            }
        });
    }
    private void showProgressDialog(boolean show, long time) {
        try {
            if (progress != null) {
                if (show) {
                    progress.show();
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            if(progress!=null && progress.isShowing()) {
                                progress.dismiss();
                            }
                        }
                    }, time);
                } else {
                    progress.dismiss();
                }
            }
        }catch(IllegalArgumentException e){
        }catch(Exception e){
        }
    }
    public String GetFileExtension(Uri uri) {

        ContentResolver contentResolver = getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;

    }
    /*
    public void spinner(){
        rootreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> mylist = new ArrayList<String>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    showProgressDialog(false,0);
                    String myvalue = postSnapshot.getKey();
                    mylist.add(myvalue);
                }
               ArrayAdapter<String> adapter=new ArrayAdapter<String>(MainActivity.this,R.layout.support_simple_spinner_dropdown_item,mylist);
                mspinner.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                showProgressDialog(false,0);
                Toast.makeText(getBaseContext(), "" + databaseError, Toast.LENGTH_SHORT).show();
            }


        });

    }
    */
}

