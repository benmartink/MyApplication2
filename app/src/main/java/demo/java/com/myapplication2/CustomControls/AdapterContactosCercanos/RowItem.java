package demo.java.com.myapplication2.CustomControls.AdapterContactosCercanos;

/**
 * Created by MARTIN on 19/08/2016.
 */
public class RowItem {
    private String title;
    private int icon;
    private String idItem;

    public RowItem(String title, String idItem) {
        this.title = title;
        this.idItem = idItem;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getIdItem() {
        return idItem;
    }

    public void setIdItem(String idItem) {
        this.idItem = idItem;
    }
}
