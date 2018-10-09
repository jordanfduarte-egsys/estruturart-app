package v3.estruturart.com.br.estruturaart.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.view.PagerAdapter;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import v3.estruturart.com.br.estruturaart.R;
import v3.estruturart.com.br.estruturaart.model.TbPedidoItemFoto;

public class GalleryAdapter extends PagerAdapter {
    private Context ctx;
    private List<TbPedidoItemFoto> fotos;

    public GalleryAdapter(Context ctx, List<TbPedidoItemFoto> fotos){
        this.ctx = ctx;
        this.fotos = fotos;
    }

    @Override
    public int getCount() {
        return getFotos().size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((TextView) object).getParent();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LinearLayout ll = new LinearLayout(ctx);
        ll.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.CENTER;
        ll.setLayoutParams(lp);
        container.addView(ll);

        ImageView iv = new ImageView(ctx);
        iv.setImageBitmap(stringToBitmap(getFotos().get(position).getBase64Imagem()));
        lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        lp.weight = 1;
        lp.gravity = Gravity.CENTER;
        iv.setBackground(ctx.getResources().getDrawable(R.drawable.border_x1));
        iv.setPadding(
            16,
            16,
            16,
            16
        );
        iv.setLayoutParams(lp);

        ll.addView(iv);

        TextView tv = new TextView(ctx);
        lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 50);
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        tv.setText("#" + getFotos().get(position).getPedidoItensIdString() + " - " + getFotos().get(position).getObservacao());
        tv.setLayoutParams(lp);
        ll.addView(tv);

        System.out.println("Script Build: Carro: "+(position));

        //return iv;
        return(tv);
    }

    protected Bitmap stringToBitmap(String content) {
        byte[] decodedString = Base64.decode(content, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object view) {
        container.removeView((View)((TextView)view).getParent());
    }

    public List<TbPedidoItemFoto> getFotos() {
        return fotos;
    }

    public void setFotos(List<TbPedidoItemFoto> fotos) {
        this.fotos.addAll(fotos);
    }
}
