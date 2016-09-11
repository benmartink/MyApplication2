package demo.java.com.myapplication2.CustomControls.AdapterContactosCercanos.AdapterMisContactos;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import demo.java.com.myapplication2.EN_Usuario;
import demo.java.com.myapplication2.R;
import demo.java.com.myapplication2.activity_detallecontacto;

public class MyAdapterContactos extends BaseAdapter {
    //private List<String> mList;
    private LayoutInflater mInflater;
    List<EN_Usuario> rowItem;
    Context context;

    public MyAdapterContactos(Context context, List<EN_Usuario> rowItem) {
        //mList = list;
        this.rowItem = rowItem;
        this.context = context;
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addItems(List<EN_Usuario> newItems) {
        if (null == newItems || newItems.size() <= 0) {
            return;
        }

        if (null == rowItem) {
            rowItem = new ArrayList<EN_Usuario>();
        }

        for (EN_Usuario object: newItems) {
            Log.i("Agregando: ", "Agregando: " + object.getNombres());
            rowItem.add(object);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (null == rowItem) {
            return 0;
        }

        return rowItem.size();
    }

    @Override
    public Object getItem(int position) {
        return rowItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView = null;
        if (null == convertView) {
            convertView = mInflater.inflate(R.layout.list_item_contacto, null);

            textView = (TextView) convertView.findViewById(R.id.nombreContacto);

            convertView.setTag(textView);
        } else {
            textView = (TextView) convertView.getTag();
        }

        ImageButton btn = (ImageButton) convertView.findViewById(R.id.btnVerDetalles);
        final EN_Usuario row_pos = rowItem.get(position);
        textView.setText(row_pos.getNombres() + " " + row_pos.getApellidos());
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                activity_detallecontacto.start(context, row_pos.getGUID() + "");

            }
        });

        ImageButton btnLlamar = (ImageButton) convertView.findViewById(R.id.btnLlamar);
        btnLlamar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + row_pos.getCelularFijo()));
                context.startActivity(intent);
            }
        });

        return convertView;
    }
}
