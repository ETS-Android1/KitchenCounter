package com.monsalud.kitchencounter.UI.Inventory.Meat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.monsalud.kitchencounter.Database.Converters;
import com.monsalud.kitchencounter.Database.KCRepository;
import com.monsalud.kitchencounter.Entity.Meat;
import com.monsalud.kitchencounter.R;
import com.monsalud.kitchencounter.Tables.InventoryItem;
import com.monsalud.kitchencounter.UI.Inventory.Beverage.BeverageInvDetailFragment;
import com.monsalud.kitchencounter.UI.Inventory.InventoryReviewActivity;
import com.monsalud.kitchencounter.UI.LoginActivity;
import com.monsalud.kitchencounter.UI.RecyclerAdapters.MeatRecyclerAdapter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class MeatInvActivity extends AppCompatActivity
    implements MeatRecyclerAdapter.MeatRecyclerListener, MeatInvDetailFragment.OnAddToInventoryListener {

    TextView mTvInventoryReview;
    MeatRecyclerAdapter mMeatRecyclerAdapter;
    KCRepository mRespository;
    ConstraintLayout meatinv;
    int mId;

    private InventoryItem.InventoryUnit mUnit;
    private InventoryItem.InventoryLocation mLocation;

    public static List<Meat> mMeats;
    private int mProdID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meat_inv);

        mTvInventoryReview = findViewById(R.id.tvReviewInv_meat);
        mTvInventoryReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MeatInvActivity.this, InventoryReviewActivity.class);
                startActivity(intent);
            }
        });
        meatinv = findViewById(R.id.meatinv);

        mRespository = new KCRepository(getApplication());
        mMeats = mRespository.getAllMeats();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.detailLayout_meat, new MeatInvDetailFragment());
        ft.replace(R.id.listLayout_meat, new MeatInvListFragment());
        ft.commit();
    }
    @Override
    public void onMeatRecyclerSent(CharSequence meatName, CharSequence meatType, int productID, InventoryItem.InventoryUnit unit) {
        MeatInvDetailFragment.updateFragment(meatName, meatType, productID, unit);
    }

    @Override
    public void onAddToInventory() {

        closeKeyboard();

        //Get the InventoryUnit value
        mUnit = MeatInvDetailFragment.mUnit;
//        int inventoryUnitPosition = MeatInvDetailFragment.mSpinUnit.getSelectedItemPosition();
//        if(inventoryUnitPosition == 0) mUnit = InventoryItem.InventoryUnit.EACH;
//        else if(inventoryUnitPosition == 1) mUnit = InventoryItem.InventoryUnit.BUNCH;
//        else if(inventoryUnitPosition == 2) mUnit = InventoryItem.InventoryUnit.CASE;
//        else if(inventoryUnitPosition == 3) mUnit = InventoryItem.InventoryUnit.KG;
//        else if(inventoryUnitPosition == 4) mUnit = InventoryItem.InventoryUnit.LB;
//        else if(inventoryUnitPosition == 5) mUnit = InventoryItem.InventoryUnit.OZ;
//        else if(inventoryUnitPosition == 6) mUnit = InventoryItem.InventoryUnit.DOZEN;
//        else mUnit = null;

        //Get the InventoryLocation value
        int inventoryLocationPosition = MeatInvDetailFragment.mSpinLocation.getFirstVisiblePosition();
        if(inventoryLocationPosition == 0)
            mLocation = InventoryItem.InventoryLocation.CENTRALWALKIN_SHELF_A;
        else if(inventoryLocationPosition == 1)
            mLocation = InventoryItem.InventoryLocation.CENTRALWALKIN_SHELF_B;
        else if(inventoryLocationPosition == 2)
            mLocation = InventoryItem.InventoryLocation.CENTRALWALKIN_SHELF_C;
        else if(inventoryLocationPosition == 3)
            mLocation = InventoryItem.InventoryLocation.CENTRALWALKIN_SHELF_D;
        else if(inventoryLocationPosition == 4)
            mLocation = InventoryItem.InventoryLocation.CENTRALWALKIN_SHELF_E;
        else if(inventoryLocationPosition == 5)
            mLocation = InventoryItem.InventoryLocation.CENTRALWALKIN_SHELF_F;
        else if(inventoryLocationPosition == 6)
            mLocation = InventoryItem.InventoryLocation.CENTRALDRYGOODS;
        else if(inventoryLocationPosition == 7)
            mLocation = InventoryItem.InventoryLocation.CENTRALHOTLINE;
        else if(inventoryLocationPosition == 8)
            mLocation = InventoryItem.InventoryLocation.CENTRALCASHIER;
        else if(inventoryLocationPosition == 9)
            mLocation = InventoryItem.InventoryLocation.PLAZAWALKIN;
        else if(inventoryLocationPosition == 10)
            mLocation = InventoryItem.InventoryLocation.PLAZADRYGOODS;
        else if(inventoryLocationPosition == 11)
            mLocation = InventoryItem.InventoryLocation.PLAZAFREEZER;
        else if(inventoryLocationPosition == 12)
            mLocation = InventoryItem.InventoryLocation.BROWNBAGMAIN;
        else if(inventoryLocationPosition == 13)
            mLocation = InventoryItem.InventoryLocation.BROWNBAGSATELLITE;
        else if(inventoryLocationPosition == 14)
            mLocation = InventoryItem.InventoryLocation.UNDERTHESTAIRS;
        else if(inventoryLocationPosition == 15)
            mLocation = InventoryItem.InventoryLocation.LOADINGDOCK;
        else mLocation = InventoryItem.InventoryLocation.OTHER;

        //Get the id number of the last InventoryItem in the arraylist and increment it
        List<InventoryItem> allInventoryItems = mRespository.getAllInventoryItems();
        if(allInventoryItems.size()>0) {
            mId = allInventoryItems.get(allInventoryItems.size() - 1).getInventoryitem_id();
            mId++;
        }
        else mId = 1;

        //Get a LocalDateTime for a timestamp
        Long nowMillis = System.currentTimeMillis();
        LocalDate nowLD = Converters.fromEpochMilliToLocalDate(nowMillis);

        //Create an InventoryItem object using the user-entered information from the detail fragment and insert it into the Database
        try {
            mProdID = Integer.parseInt(String.valueOf(MeatInvDetailFragment.mTvProductID.getText()));
        } catch(Exception e) {
            showErrorMsg();
        }

        try {
            InventoryItem item = new InventoryItem(
                    mId,
                    mProdID,
                    LoginActivity.mEmployeeID,
                    nowLD,
                    Double.parseDouble(String.valueOf(MeatInvDetailFragment.mEtAmount.getText())),
                    mLocation,
                    false, true);

            mRespository.insertInventoryItem(item);

            //Create a snackbar that confirms the transaction
            confirmInventory();

        } catch (Exception e) {
            e.printStackTrace();

            //Snackbar error message
            showErrorMsg();
        }
    }
    private void closeKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = this.getCurrentFocus();
        if (view == null) view = new View(this);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    private void showErrorMsg() {
        Snackbar errorInsertInventory = Snackbar.make(meatinv, "There was an error submitting this item", Snackbar.LENGTH_LONG);
        errorInsertInventory.setBackgroundTint(Color.WHITE);
        TextView errorText = (TextView) (errorInsertInventory.getView()).findViewById(com.google.android.material.R.id.snackbar_text);
        errorText.setTextSize(32);
        errorText.setTextColor(Color.BLACK);
        errorInsertInventory.show();
    }
    private void confirmInventory() {
        Snackbar confirmationInventory = Snackbar.make(meatinv, "This item has been submitted", Snackbar.LENGTH_LONG);
        confirmationInventory.setBackgroundTint(Color.WHITE);
        TextView confirmText = (TextView) (confirmationInventory.getView()).findViewById(com.google.android.material.R.id.snackbar_text);
        confirmText.setTextSize(32);
        confirmText.setTextColor(Color.BLACK);
        confirmationInventory.show();
    }
}