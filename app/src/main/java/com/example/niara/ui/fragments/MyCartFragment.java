package com.example.niara.ui.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.niara.Adapters.CartAdapter;
import com.example.niara.Api.ApiClient;
import com.example.niara.Api.ApiInterface;
import com.example.niara.Model.CartInfo;
import com.example.niara.Model.Food;
import com.example.niara.R;
import com.example.niara.ui.activities.LoginActivity;
import com.example.niara.ui.activities.MainActivity;
import com.example.niara.ui.activities.PaymentActivity;
import com.example.niara.ui.activities.ProductDesc;
import com.example.niara.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyCartFragment extends Fragment {
    public RecyclerView rcFoodCart;
    public static ArrayList<JSONObject> userCartDetailsList;
    public static ArrayList<JSONObject> productList;
    public static ArrayList<JSONObject> cartProducts;
    public int userCart;
    public TextView tvSubtotal;
    public TextView tvTotal;
    public Button btnCheckout;
    public RelativeLayout nodisplay;
    public ScrollView cartscroll;


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public MyCartFragment() {
        // Required empty public constructor
    }


    public static MyCartFragment newInstance(String param1, String param2) {
        MyCartFragment fragment = new MyCartFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_cart, container, false);

        SessionManager sessionManager = new SessionManager(getContext());
        userCart = sessionManager.getUserid();

        tvSubtotal = view.findViewById(R.id.tv_subTotal);
        tvTotal = view.findViewById(R.id.tv_total_price);
        btnCheckout = view.findViewById(R.id.btn_checkout);
        getUserFromCart(tvTotal,tvSubtotal,btnCheckout);
        nodisplay=view.findViewById(R.id.nothingaddedincart);
        cartscroll=view.findViewById(R.id.cartScroll);
        rcFoodCart = view.findViewById(R.id.rc_food_cart);
        rcFoodCart.setLayoutManager(new LinearLayoutManager(this.getContext(),RecyclerView.VERTICAL,false));
        return view;
    }


    public void getUserFromCart(TextView tvTotal, TextView tvSubtotal, Button btnCheckout) {
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading Cart");
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<CartInfo>> getUserCart = apiInterface.getCartDetails();
        userCartDetailsList = new ArrayList<>();

        getUserCart.enqueue(new Callback<ArrayList<CartInfo>>() {
            @Override
            public void onResponse(Call<ArrayList<CartInfo>> call, Response<ArrayList<CartInfo>> response) {
                if (response.isSuccessful() && response.body()!=null) {
                    for (int i = 0; i < response.body().size(); i++) {
                        if (response.body().get(i).getUser() == userCart) {
                            JSONObject ob = new JSONObject();
                            try {
                                ob.put("id", response.body().get(i).getCartId());
                                ob.put("user", response.body().get(i).getUser());
                                ob.put("product", response.body().get(i).getProduct());
                                ob.put("quantity", response.body().get(i).getQuantity());
                                userCartDetailsList.add(ob);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    if (userCartDetailsList!=null){
                        getProduct(userCartDetailsList,tvTotal,tvSubtotal,btnCheckout,progressDialog);
                    }
                    else{

                    }
                } else {
                    Toast.makeText(getContext(),"Something Went Wrong",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ArrayList<CartInfo>> call, Throwable t) {
                Toast.makeText(getContext(),"Something Went Wrong",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getProduct(ArrayList<JSONObject> userCartDetailsList, TextView tvTotal, TextView tvSubtotal, Button btnCheckout, ProgressDialog progressDialog)  {
        if (userCartDetailsList.size()!=0){
            ApiInterface apiInterfaceCart = ApiClient.getClient().create(ApiInterface.class);
            productList =  new ArrayList<>();
            cartProducts = new ArrayList<>();
            int subTotalPrice = 0,subTotal = 0;
            for (int i = 0; i < userCartDetailsList.size(); i++) {

                try {
                    int productId = (int) userCartDetailsList.get(i).get("product");
                    JSONObject object = new JSONObject();
                    object.put("id",userCartDetailsList.get(i).get("id"));
                    object.put("user", userCartDetailsList.get(i).get("user"));
                    object.put("quantity", userCartDetailsList.get(i).get("quantity"));
                    Call<Food> getUserProduct = apiInterfaceCart.getProductList(productId);
                    getUserProduct.enqueue(new Callback<Food>() {
                        @Override
                        public void onResponse(Call<Food> call, Response<Food> response) {
                            if(response.isSuccessful()){
                                progressDialog.hide();
                                assert response.body() != null;

                                try {
                                    object.put("productId", response.body().getId());
                                    object.put("title", response.body().getTitle());
                                    object.put("Product_quantity", response.body().getProduct_quantity());
                                    object.put("selling_price", response.body().getSelling_price());
                                    object.put("discounted_price", response.body().getDiscounted_price());
                                    object.put("description", response.body().getDescription());
                                    object.put("brand", response.body().getBrand());
                                    object.put("category", response.body().getCategory());
                                    object.put("product_image", response.body().getProduct_image());
                                    cartProducts.add(object);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                Log.d("CartList", "Response:" + cartProducts.toString());
                                loadRecView(cartProducts,tvTotal,tvSubtotal,btnCheckout);
                            }else{
                            }
                        }

                        @Override
                        public void onFailure(Call<Food> call, Throwable t) {
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }else {
            Toast.makeText(getContext(),"Cart is Empty",Toast.LENGTH_LONG).show();
            Log.d("callingthis","hellofalse");
            progressDialog.hide();
            cartscroll.setVisibility(View.GONE);
            nodisplay.setVisibility(View.VISIBLE);
        }

    }

    private void loadRecView(ArrayList<JSONObject> cartProducts,  TextView tvTotal, TextView tvSubtotal, Button btnCheckout) {
        int subTotalPrice = 0,subTotal = 0;
        for (int i = 0;i<cartProducts.size();i++){
            try {
                subTotalPrice = Integer.parseInt(cartProducts.get(i).getString("quantity")) * Integer.parseInt(cartProducts.get(i).getString("discounted_price")) ;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            subTotal = subTotal + subTotalPrice;
        }
        tvSubtotal.setText(String.valueOf(subTotal));
        tvTotal.setText(String.valueOf(subTotal+70));

        CartAdapter cartAdapter = new CartAdapter(getContext(),cartProducts);
        rcFoodCart.setAdapter(cartAdapter);

        cartAdapter.setCartListener(position -> {
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            try {
                Toast.makeText(getContext(), String.valueOf(position)+" "+cartProducts.get(position).get("title")+" " + cartProducts.get(position).get("id") ,Toast.LENGTH_SHORT).show();
                Call<Void> removeCartItems = apiInterface.deleteCartItems((Integer) cartProducts.get(position).get("id"));
                Log.e("CartListRemove",  cartProducts.get(position).toString());
                removeCartItems.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()){
                            Toast.makeText(getContext(), "Removed Successfully!!",Toast.LENGTH_SHORT).show();
                            cartProducts.remove(position);
                            int subTotalPrice = 0,subTotal = 0;
                            for(int j=0;j<cartProducts.size();j++){
                                try {
                                    subTotalPrice = Integer.parseInt(cartProducts.get(j).getString("quantity")) * Integer.parseInt(cartProducts.get(j).getString("discounted_price")) ;
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            subTotal = subTotal + subTotalPrice;
                            tvSubtotal.setText(String.valueOf(subTotal));
                            tvTotal.setText(String.valueOf(subTotal+70));
//                            Log.e("CartListRemove",  cartProducts.get(position).toString());
                            cartAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(getContext(), "Something Went Wrong",Toast.LENGTH_SHORT).show();
                    }
                });
            }catch (JSONException e) {
                e.printStackTrace();
            }
        });



        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.valueOf(tvTotal.getText().toString())>70){
                    Intent intent = new Intent(getContext(), PaymentActivity.class);
                    intent.putExtra("amount",tvTotal.getText().toString());
                    intent.putExtra("subtotalamount",tvSubtotal.getText().toString());
                    startActivity(intent);
                }
            }
        });

    }
}
