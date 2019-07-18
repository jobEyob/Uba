package com.ezyro.uba_inventory;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.ExpandableDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;


public class DrawerUtil {
    public static void getDrawer(final Activity activity, Toolbar toolbar) {

        // Create the AccountHeader
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(activity)
                .withHeaderBackground(R.drawable.nave)
                .addProfiles(
                       new ProfileDrawerItem()
                               //.withName("Mike Penz")
                               .withEmail(R.string.nav_header_subtitle)
                               .withIcon(R.drawable.uba_logo)
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {


                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                        return false;
                    }
                })

	.build();

        //if you want to update the items at a later time it is recommended to keep it in a variable
//        PrimaryDrawerItem drawerEmptyItem= new PrimaryDrawerItem().withIdentifier(0).withName("");
//        drawerEmptyItem.withEnabled(false);

        PrimaryDrawerItem drawerItemHome = new PrimaryDrawerItem().withIdentifier(1)
                .withName(R.string.nave_activity_title_Home).withIcon(R.drawable.ic_home);
        PrimaryDrawerItem drawerItemProductList = new PrimaryDrawerItem()
                .withIdentifier(2).withName(R.string.nave_activity_title_product_list).withIcon(R.drawable.ic_menu_camera);


        PrimaryDrawerItem drawerItemAddProduct = new PrimaryDrawerItem().withIdentifier(3)
                .withName(R.string.editor_activity_and_nave_title_add_product).withIcon(R.drawable.ic_menu_manage);
        PrimaryDrawerItem drawerItemCategory = new PrimaryDrawerItem().withIdentifier(4)
                .withName(R.string.nave_activity_title_product_category).withIcon(R.drawable.ic_menu_share);
        PrimaryDrawerItem drawerItemItem = new PrimaryDrawerItem().withIdentifier(5)
                .withName(R.string.nave_activity_title_product_item).withIcon(R.drawable.ic_menu_slideshow);

        SectionDrawerItem sectionDrawerItemCommun = new SectionDrawerItem().withName("Communicate");

        PrimaryDrawerItem drawerItemSupplier = new PrimaryDrawerItem().withIdentifier(6)
                .withName(R.string.nave_supplier).withIcon(R.drawable.ic_menu_send);

        PrimaryDrawerItem drawerItemOrders = new PrimaryDrawerItem().withIdentifier(7)
                .withName(R.string.nave_order_product).withIcon(R.drawable.ic_menu_send);

        SectionDrawerItem sectionDrawerItemReport = new SectionDrawerItem().withName("Report");

         PrimaryDrawerItem drawerItemStatistics = new PrimaryDrawerItem().withIdentifier(8)
                .withName(R.string.nave_statistics).withIcon(R.drawable.ic_menu_send);

        PrimaryDrawerItem drawerItemExport = new PrimaryDrawerItem().withIdentifier(9)
                .withName(R.string.nave_export_data).withIcon(R.drawable.ic_menu_send);

        SectionDrawerItem sectionDrawerItemUsers = new SectionDrawerItem().withName("Users");

//        SecondaryDrawerItem drawerItemAdduser = new SecondaryDrawerItem().withIdentifier(10).withName(R.string.nave_add_users).withIcon(R.drawable.ic_menu_send);
//        SecondaryDrawerItem drawerItemMangeuser = new SecondaryDrawerItem().withIdentifier(11).withName(R.string.nave_manage_users).withIcon(R.drawable.ic_menu_send);

        ExpandableDrawerItem expandableDrawerItem = new ExpandableDrawerItem().withName(R.string.users).withIcon(R.drawable.ic_menu_gallery).withIdentifier(19).withSelectable(false).withSubItems(
                new SecondaryDrawerItem().withName(R.string.nave_add_users).withLevel(2).withIcon(R.drawable.ic_menu_slideshow).withIdentifier(10),
                new SecondaryDrawerItem().withName(R.string.nave_manage_users).withLevel(2).withIcon(R.drawable.ic_menu_share).withIdentifier(11)
        );



        //create the drawer and remember the `Drawer` result object
        Drawer result = new DrawerBuilder()
                .withActivity(activity)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true)
                .withCloseOnClick(true)
                .withSelectedItem(-1)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                       // drawerEmptyItem,drawerEmptyItem,drawerEmptyItem,
                        drawerItemHome,
                        drawerItemProductList,
                        drawerItemCategory,
                        drawerItemAddProduct,
                        drawerItemItem,
                        sectionDrawerItemCommun,
                        new DividerDrawerItem(),
                        drawerItemSupplier,
                        drawerItemOrders,
                        sectionDrawerItemReport,
                        new DividerDrawerItem(),
                        drawerItemStatistics,
                        drawerItemExport,
                        sectionDrawerItemUsers,
                       //new DividerDrawerItem(),
                        expandableDrawerItem

                        //drawerItemAdduser,
                       // drawerItemMangeuser

                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem.getIdentifier() == 1 && !(activity instanceof MainActivity)) {
                            // load tournament screen
                            Intent intent = new Intent(activity, MainActivity.class);
                            view.getContext().startActivity(intent);
                        }else if(drawerItem.getIdentifier() == 2 && !(activity instanceof  CatalogActivity)){
                            Intent intent = new Intent(activity, CatalogActivity.class);
                            view.getContext().startActivity(intent);
                        }
                        else if(drawerItem.getIdentifier() == 3 && !(activity instanceof EditorActivity)){
                            Intent intent = new Intent(activity, EditorActivity.class);
                            view.getContext().startActivity(intent);
                        }
                        else if(drawerItem.getIdentifier() == 4 && !(activity instanceof CategoryActivity)){
                            Intent intent = new Intent(activity, CategoryActivity.class);
                            view.getContext().startActivity(intent);
                        }
                        else if(drawerItem.getIdentifier() == 5 && !(activity instanceof ItemActivity)){
                            Intent intent = new Intent(activity, ItemActivity.class);
                            view.getContext().startActivity(intent);
                        }
                        else if(drawerItem.getIdentifier() == 6 && !(activity instanceof SupplierActivity)){
                            Intent intent = new Intent(activity, SupplierActivity.class);
                            view.getContext().startActivity(intent);
                        }
//                        else if(drawerItem.getIdentifier() == 7 && !(activity instanceof OrederActivity)){
//                            Intent intent = new Intent(activity, OrederActivity.class);
//                            view.getContext().startActivity(intent);
//                        }
//                        else if(drawerItem.getIdentifier() == 8 && !(activity instanceof StatisticsActivity)){
//                            Intent intent = new Intent(activity, StatisticsActivity.class);
//                            view.getContext().startActivity(intent);
//                        }
//                        else if(drawerItem.getIdentifier() == 9 && !(activity instanceof ExportActivity)){
//                            Intent intent = new Intent(activity, ExportActivity.class);
//                            view.getContext().startActivity(intent);
//                        }
//                        else if(drawerItem.getIdentifier() == 10 && !(activity instanceof AddUserActivity)){
//                            Intent intent = new Intent(activity, AddUserActivity.class);
//                            view.getContext().startActivity(intent);
//                        }
//                        else if(drawerItem.getIdentifier() == 11 && !(activity instanceof MangeUserActivity)){
//                            Intent intent = new Intent(activity, MangeUserActivity.class);
//                            view.getContext().startActivity(intent);
//                        }



                        return true;
                    }
                })
                .build();
    }
}
