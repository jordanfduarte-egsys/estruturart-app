package v3.estruturart.com.br.estruturaart.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import com.basgeekball.awesomevalidation.ValidationHolder;
import com.basgeekball.awesomevalidation.utility.custom.CustomErrorReset;
import com.basgeekball.awesomevalidation.utility.custom.CustomValidation;
import com.basgeekball.awesomevalidation.utility.custom.CustomValidationCallback;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.codec.binary.Base32;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import v3.estruturart.com.br.estruturaart.R;
import v3.estruturart.com.br.estruturaart.model.CepModel;
import v3.estruturart.com.br.estruturaart.model.Orcamento;
import v3.estruturart.com.br.estruturaart.model.TbCidade;
import v3.estruturart.com.br.estruturaart.model.TbEndereco;
import v3.estruturart.com.br.estruturaart.model.TbEstado;
import v3.estruturart.com.br.estruturaart.model.TbPedido;
import v3.estruturart.com.br.estruturaart.model.TbPedidoItem;
import v3.estruturart.com.br.estruturaart.model.TbPerfil;
import v3.estruturart.com.br.estruturaart.model.TbUsuario;
import v3.estruturart.com.br.estruturaart.service.Client;
import v3.estruturart.com.br.estruturaart.utility.AsyncResponse;
import v3.estruturart.com.br.estruturaart.utility.AsyncTaskCustom;
import v3.estruturart.com.br.estruturaart.utility.JsonModel;
import v3.estruturart.com.br.estruturaart.utility.MaskEditUtil;
import v3.estruturart.com.br.estruturaart.utility.Param;

public class DetalhePedido extends AbstractActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener, AsyncResponse {

    private TbPedido pedido = new TbPedido();
    private Integer idCameraItem = 0;
    private String imagemBase64 = "";
    private JsonModel retornoCamera = new JsonModel();
    private String message = "";
    private static final int ASYNC_SEND_PHOTO = 1;
    private static final int ASYNC_PEDIDO = 2;
    private static final int PHOTO_OK = 1245;
    private boolean isCamera = false;
    private String observacao = "";
    private String mCurrentPhotoPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Metodos iniciais
        super.complementOnCreate();

        // Atribui qual é a view
        setContentView(R.layout.activity_detalhe_pedido);

        // Recupera o id do pedido
        pedido.setId(Integer.parseInt(getIntent().getExtras().getString("id")));

        PackageManager pm = this.getPackageManager();

        if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            isCamera = true;
        }

        //Busca o pedido
        findPedido();

        initNavigationBar().setNavigationItemSelectedListener(this);
    }
        @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            return onNavigationItemSelectedActions(item);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public String onExecTask(String result, int id) {
        if (id == ASYNC_PEDIDO) {
            Client client = new Client(this);
            client.getParameter().put("id", String.valueOf(pedido.getId()));
            pedido = (TbPedido) client.fromPost("/detalhe-pedido", TbPedido.class);

            if (!client.getMessage().equals("")) {
                message = client.getMessage();
            }
        } else if (id == ASYNC_SEND_PHOTO) {
            Client client = new Client(this);
            client.getParameter().put("id", String.valueOf(idCameraItem));
            client.getParameter().put("base64", imagemBase64);
            client.getParameter().put("format", "jpg");
            client.getParameter().put("observacao", observacao);
            System.out.println("ID: " + idCameraItem);
            //System.out.println("\n\nCADE O BASE: " + imagemBase64);

            retornoCamera = (JsonModel) client.fromPost("/nova-foto-camera-item", JsonModel.class);
            if (!client.getMessage().equals("")) {
                message = client.getMessage();
            }
        }
        return null;
    }

    @Override
    public String onPreTask(String result, int id) {
        getProgressBar(R.id.progressBar1).setVisibility(View.VISIBLE);
        return null;
    }

    @Override
    public String onPosTask(String result, int id) {
        if (id == ASYNC_PEDIDO) {
            popularPedido();
        } else if (id == ASYNC_SEND_PHOTO) {
            imagemBase64 = "";
            idCameraItem = 0;
            mCurrentPhotoPath = "";
            if (!retornoCamera.getMessage().equals("")) {
                showMessage(this, retornoCamera.getMessage());
                retornoCamera = new JsonModel();
            }
        }

        if (!message.equals("")) {
            showMessage(this, message);
            message = "";
        }
        getProgressBar(R.id.progressBar1).setVisibility(View.GONE);
        return null;
    }

    public void findPedido() {
        AsyncTaskCustom async = new AsyncTaskCustom(ASYNC_PEDIDO);
        async.delegate = (AsyncResponse) this;
        async.execute();
    }

    public void popularPedido() {
        if (pedido.getId() == 0) {
            showMessage(this, "Ocorreu um erro ao consultar o pedido. Tente novamente mais tarde!");
            finish();
        }
        getTextView(R.id.numPedido).setText("Pedido #" + pedido.getIdString());
        getTextView(R.id.totalItens).setText("Total itens: " + pedido.getItens().size());
        getTextView(R.id.precoItensTotal).setText("Preço total itens: R$ " + pedido.getPrecoGeralString());
        getTextView(R.id.desconto).setText("Desconto: " + pedido.getDescontoGeralString() + "%");
        getTextView(R.id.valorMaoObra).setText("Mão obra: R$ " + pedido.getValorMaoObraString());
        getTextView(R.id.precoTotalPedido).setText("Preço total: R$ " + pedido.getPrecoGeralMaisMaoObraString());
        getTextView(R.id.enderecoCompleto1).setText(String.format("%s %s", pedido.getEndereco().getLogradouro(), pedido.getEndereco().getNumero()));
        getTextView(R.id.enderecoCompleto2).setText(String.format("Cep: %s %s/%s - %s", pedido.getEndereco().getCep(), pedido.getEndereco().getCidade().getNome(), pedido.getEndereco().getCidade().getEstado().getNome(), pedido.getEndereco().getComplemento()));
        getTextView(R.id.nomeClienteCpf).setText(String.format("%s - %s", pedido.getUsuario().getNome(), pedido.getUsuario().getCpfCnpjString()));
        getTextView(R.id.statusNome).setText(pedido.getStatusPedido().getNome());
        getTextView(R.id.previsao).setText("Prev. Inst.: " + pedido.getDataPrevisaoInstalacaoString());
        getTextView(R.id.previsao).setBackgroundColor(getResources().getColor(pedido.getCorPrevisaoInstalacaoInt()));
        if (!pedido.getObservacao().equals("")) {
            getTextView(R.id.observacao).setText(pedido.getObservacao());
        } else {
            getTextView(R.id.observacao).setVisibility(View.GONE);
        }

        TableLayout tl = (TableLayout)findViewById(R.id.tbListItensDetalhePedido);
        for (TbPedidoItem item : pedido.getItens()) {
            View rowModelo = getLayoutInflater().inflate(R.layout.lista_itens_detalhe_pedido, null);

            ((TextView)rowModelo.findViewById(R.id.numItem)).setText("#" + item.getIdString());
            ((TextView)rowModelo.findViewById(R.id.nomeModelo)).setText("#" + item.getModelo().getNomeString() + " - " + item.getModelo().getDimensao());
            ((TextView)rowModelo.findViewById(R.id.isPinturaString)).setText((item.getIsPintura() ? "Com pintura" : "Sem pintura"));
            ((TextView)rowModelo.findViewById(R.id.descModelo)).setText(item.getModelo().getDescricao());
            ((TextView)rowModelo.findViewById(R.id.precoItem)).setText("Preço: R$ " + item.getPrecoItemMaisPintura());
            ((TextView)rowModelo.findViewById(R.id.quantidadeCompra)).setText("Qtd: " + item.getQuantidade() + "un.");

            tl.addView(rowModelo);

            BootstrapButton btn = (BootstrapButton) rowModelo.findViewById(R.id.btPhoto);
            if (isCamera) {
                btn.setTag(item);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        modalGaleriaOuFoto(view);
                    }
                });
            } else {
                btn.setVisibility(View.GONE);
            }
        }
    }

    public void modalGaleriaOuFoto(final View v) {
        TbPedidoItem item = (TbPedidoItem)v.getTag();
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext()).setTitle("Foto")
            .setMessage(String.format("Deseja tirar uma nova foto ou acessar a galeria do item #%s ?", item.getIdString()))
            .setIcon(android.R.drawable.ic_menu_camera)//@ICONE DE FOTO
            .setPositiveButton("Foto", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    acessarCamera(v);
                }
            })
            .setNegativeButton("Galeria", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    acessarGaleria(v);
                }
            });
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorAccent));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorAccent));
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",         /* suffix */
            storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void acessarCamera(View v) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        idCameraItem = ((TbPedidoItem)v.getTag()).getId();
        //startActivityForResult(i, PHOTO_OK);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) { }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                    "v3.estruturart.com.br.estruturaart",
                    photoFile
                );
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, PHOTO_OK);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //@see https://forums.bignerdranch.com/t/imageview-showing-in-landscape-but-not-portrait/7689/7
        if (requestCode == PHOTO_OK) {
            if (resultCode == Activity.RESULT_OK) {
                final View rowModelo = getLayoutInflater().inflate(R.layout.image_layout, null);
                ImageView mImageView = (ImageView)rowModelo.findViewById(R.id.viewImagem);

                final Dialog settingsDialog = new Dialog(DetalhePedido.this, R.style.Theme_Dialog);
                settingsDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.MATCH_PARENT);
                settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

                settingsDialog.setCancelable(true);
                settingsDialog.setCanceledOnTouchOutside(true);
                settingsDialog.setContentView(rowModelo);

                settingsDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        imagemBase64 = "";
                        mCurrentPhotoPath = "";
                        idCameraItem = 0;
                    }
                });
                settingsDialog.show();
                BootstrapButton btn = (BootstrapButton)rowModelo.findViewById(R.id.btEnviarImagem);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        observacao = ((EditText)(rowModelo.findViewById(R.id.obsImagem))).getText().toString();
                        enviarArteParaServidor(view);
                        settingsDialog.dismiss();
                    }
                });
                int orientation;
                String path = mCurrentPhotoPath;
                try {
                    // decode image size
                    BitmapFactory.Options o = new BitmapFactory.Options();
                    o.inJustDecodeBounds = true;
                    // Find the correct scale value. It should be the power of 2.
                    final int REQUIRED_SIZE = 70;
                    int width_tmp = o.outWidth, height_tmp = o.outHeight;
                    int scale = 0;
                    while (true) {
                        if (width_tmp / 2 < REQUIRED_SIZE
                                || height_tmp / 2 < REQUIRED_SIZE)
                            break;
                        width_tmp /= 2;
                        height_tmp /= 2;
                        scale++;
                    }
                    // decode with inSampleSize
                    BitmapFactory.Options o2 = new BitmapFactory.Options();
                    o2.inSampleSize = scale;
                    Bitmap bm = BitmapFactory.decodeFile(path, o2);
                    Bitmap bitmap = bm;

                    ExifInterface exif = new ExifInterface(path);

                    orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

                    //exif.setAttribute(ExifInterface.ORIENTATION_ROTATE_90, 90);

                    Matrix m = new Matrix();

                    if ((orientation == ExifInterface.ORIENTATION_ROTATE_180)) {
                        m.postRotate(180);
                        //m.postScale((float) bm.getWidth(), (float) bm.getHeight());
                        // if(m.preRotate(90)){
                        bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),bm.getHeight(), m, true);
                    } else if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                        m.postRotate(90);
                        bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),bm.getHeight(), m, true);
                    }
                    else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                        m.postRotate(270);
                        bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),bm.getHeight(), m, true);
                    }

                    mImageView.setImageBitmap(bitmap);

                    Bitmap immagex = bitmap;
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    immagex.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] b = baos.toByteArray();
                    Base32 b2 = new Base32();
                    imagemBase64 = b2.encodeAsString (b);
                } catch (Exception e) {
                    showMessage(this, "Opss. Ocorreu um erro ao capturar a foto!");
                }
            } else {
                imagemBase64 = "";
                mCurrentPhotoPath = "";
                idCameraItem = 0;
            }
        }
    }

    public void acessarGaleria(View v) {
        Intent intent = new Intent(v.getContext(), Galeria.class);
        intent.putExtra("id", String.valueOf(((TbPedidoItem)v.getTag()).getId()));
        startActivity(intent);
    }

    public void enviarArteParaServidor(View v) {
        AsyncTaskCustom async = new AsyncTaskCustom(ASYNC_SEND_PHOTO);
        async.delegate = (AsyncResponse) this;
        async.execute();
    }
}