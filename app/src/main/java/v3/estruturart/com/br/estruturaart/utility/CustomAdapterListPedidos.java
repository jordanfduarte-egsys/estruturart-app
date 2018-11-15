package v3.estruturart.com.br.estruturaart.utility;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapText;
import com.beardedhen.androidbootstrap.api.view.BootstrapTextView;
import com.beardedhen.androidbootstrap.font.FontAwesome;

import java.util.ArrayList;

import v3.estruturart.com.br.estruturaart.R;
import v3.estruturart.com.br.estruturaart.model.TbPedido;

public class CustomAdapterListPedidos extends ArrayAdapter<TbPedido> implements View.OnClickListener{

    private ArrayList<TbPedido> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        BootstrapTextView txtName;
        TextView txtType;
        TextView txtVersion;
        ImageView info;
    }

    public CustomAdapterListPedidos(ArrayList<TbPedido> data, Context context) {
        super(context, R.layout.item_list_pedidos, data);
        this.dataSet = data;
        this.mContext=context;

    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        TbPedido dataModel=(TbPedido)object;

        switch (v.getId())
        {
            case R.id.item_info:
//                Snackbar.make(v, "Release date " +dataModel.getFeature(), Snackbar.LENGTH_LONG)
//                    .setAction("No action", null).show();
                break;
        }
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        TbPedido dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_list_pedidos, parent, false);

            viewHolder.txtName = (BootstrapTextView) convertView.findViewById(R.id.name);
            viewHolder.txtType = (TextView) convertView.findViewById(R.id.type);
            viewHolder.txtVersion = (TextView) convertView.findViewById(R.id.version_number);
            viewHolder.info = (ImageView) convertView.findViewById(R.id.item_info);

            result=convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(
            mContext, (position > lastPosition) ? R.anim.up_from_top : R.anim.down_from_top
        );
        result.startAnimation(animation);
        lastPosition = position;

        BootstrapText text = new BootstrapText.Builder(mContext)
            .addFontAwesomeIcon(FontAwesome.FA_SLACK)
            .addText(" " + dataModel.getIdString())
            .build();

        viewHolder.txtName.setBootstrapText(text);
        viewHolder.txtType.setText(dataModel.getUsuario().getNome());
        viewHolder.txtVersion.setText(dataModel.getDataPrevisaoInstalacaoString());
        viewHolder.txtVersion.setBackgroundColor(mContext.getResources().getColor(dataModel.getCorPrevisaoInstalacaoInt()));

        viewHolder.info.setOnClickListener(this);
        viewHolder.info.setTag(position);
        return convertView;
    }
}
