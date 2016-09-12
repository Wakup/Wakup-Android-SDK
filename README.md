Wakup SDK Library
==================

[![Download](https://api.bintray.com/packages/wakup/sdk/android-sdk/images/download.svg)](https://bintray.com/wakup/sdk/android-sdk/_latestVersion) [ ![Method Count](https://img.shields.io/badge/Methods count-core: 1789 | deps: 27515-e91e63.svg)](http://www.methodscount.com/?lib=com.wakup.android%3Asdk%3A2.0.0)

Native Android SDK for [Wakup platform](http://wakup.net).

## Installation

To start using Wakup you have to integrate the Wakup SDK in your Android Application.

### Gradle dependency

Include this dependency in the `build.gradle` file of your application project module.

```groovy
dependencies {
    compile 'com.wakup.android:sdk:2.0.0'
}
```

### Google Maps

Wakup uses the Google Maps library to display geo-located offers in a Map View.
To setup the Google Maps API in your application you can following the [official documentation](https://developers.google.com/maps/documentation/android-api/start).

**Note:** It is important to remark that you will need to give access to the API Key for your debug and release certificates.

### Start Wakup Activity

To start the main Wakup activity, that will be the entry point for the entire offers section, you will need to provide your Wakup API Key, wrapped in a `WakupOptions` object:

```java
// Launch Wakup with custom region and default location
Wakup.instance(this).launch(
        new WakupOptions("WAKUP_API_KEY").          // Auth API Key
                country("ES").                      // Region for address search
                defaultLocation(41.38506, 2.17340). // For disabled location devices
                showBackInRoot(true)                // Display or not back button in Wakup
);

```

Trough the `WakupOptions` object it is possible to setup the following parameters:

| Parameter | Description |
|-----------|-------------|
| **apiKey** | Included in the constructor, is the authentication token for your application |
| **country** | [ISO country code](https://en.wikipedia.org/wiki/ISO_3166-1) used for region filter on places search. Default is `"ES"`.
| **defaultLocation** | Default coordinates when device location is not available. Default location is Madrid.
| **showBackInRoot** | Defines if the back navigation button should be displayed in Wakup Main activity. Default is `false`.
| **categories** | List of categories to be used as filter in search results. If null of empty, no filters will be displayed.
| **mapMarkers** | Customized markers to represent offers in Map View.

## Customization

Wakup uses its own theme with preset styles, icons and colors that can be easily customized.
The default Wakup appearance looks like the following:

[![](http://i.imgur.com/r48ftsEm.png)](http://i.imgur.com/r48ftsE.png) [![](http://i.imgur.com/WvISmNbm.png)](http://i.imgur.com/WvISmNb.png) [![](http://i.imgur.com/Rnz2cEfm.png)](http://i.imgur.com/Rnz2cEf.png)

The application appearance can be customized by overriding the resources used by the styles and layouts of the Wakup SDK. This resources contains the prefix `'wk_'` to avoid clashing with client application resources.

The elements that are not customized will be displayed will the default look & feel.

We suggest to create a resources XML file called `wakup.xml` in your `res/values` folder that will contain all the customized resources.

### Strings

Wakup uses I18n string resources for the all the texts displayed in the application, so it can be overriden to customize the messages shown.

For example, to change the title of the Wakup activities, include and change this resource strings in your `wakup.xml` file:

```xml
<!-- Activity titles -->
<string name="wk_activity_offers">Ofertas</string>
<string name="wk_activity_my_offers">Mis ofertas</string>
<string name="wk_activity_offer_detail">Oferta</string>
<string name="wk_activity_big_offer">Ofertón</string>
<string name="wk_activity_map">Mapa</string>
<string name="wk_activity_report">Informar de un error</string>
```

You can access to the full list of string resources used by the SDK [here](https://github.com/Wakup/Wakup-Android-SDK/blob/master/sdk/src/main/res/values/strings.xml).

### Icons

To display icons in the correct color, Wakup SDK use filters to apply tints depending on the icon current state. For example, button icons could be disabled, pressed or in its default state and its color will change accordingly.

To correctly apply the different colors, the original images must be **white colored** PNG images with **transparent background**.

![](http://i.imgur.com/ctkKci5.png)

The only exceptions to this behavior are:

- The map pins for Google Map, that use a semi-transparent layer that can not be tinted
- The ActionBar logo and menu icons, that will be displayed with their original color

#### Customization

The platform uses icons referenced by drawable resources that can be easily overriden with references to another customized icons.

For example, to set the big offer icon in the navigation bar, you only have to include this drawable resources in your `wakup.xml` file:

```xml
<!-- Navigation -->
<drawable name="wk_nav_big_offer">@drawable/wk_ic_nav_big_offer</drawable>
```


The complete list of drawable resources used in Wakup can be found [here](https://github.com/Wakup/Wakup-Android-SDK/blob/master/sdk/src/main/res/values/icons.xml).

### Colors

Main application Look & Feel will be customized by overriding the default colors used by the Wakup SDK layout for **views**, **icons** and **text fonts**.

To do so, copy and alter the primary colors on your `wakup.xml` file:

```xml
<!-- Primary customization colors -->
<color name="wk_primary">#3C1E3D</color>
<color name="wk_primary_pressed">#7B4C7D</color>
<color name="wk_primary_reverse">#A47BA6</color>
<color name="wk_primary_dark">#321933</color>
<color name="wk_secondary">#809718</color>
<color name="wk_secondary_pressed">#617213</color>
<color name="wk_secondary_reverse">@color/wk_white</color>
```

This main colors are used as a reference of the entire Offers section and will determine its appearance.

By changing this resources, you will override, **at compile time** the color used by the Wakup section.

Complete list of resource colors used in the project can be found [here](https://github.com/Wakup/Wakup-Android-SDK/blob/master/sdk/src/main/res/values/colors.xml).

### Fonts

Wakup SDK provides a method to customize easily the typeface used in the application by following two setps: 

1. Copy your font files to the `assets/fonts` folder of your project module
2. Override the following list of String resources (that currently contains the path of the default Wakup fonts) with the path of the file for each font format:

```xml
<string name="wk_font_default">fonts/Aller_Lt.ttf</string>
<string name="wk_font_regular">fonts/Aller_Rg.ttf</string>
<string name="wk_font_italic">fonts/Aller_LtIt.ttf</string>
<string name="wk_font_bold">fonts/Aller_Bd.ttf</string>
<string name="wk_font_short_offer">fonts/AllerDisplay.ttf</string>
```

Note that the Category object constructor takes the Array of tags as a [vararg](http://docs.oracle.com/javase/tutorial/java/javaOO/arguments.html#varargs) that allows to include all the tags as seperated parameters avoiding the need of creating a `String[]` instance.


### Deep customization

If a more thorough customization is required, you can also override the secondary colors (that are mostly based in previously defined primary colors) that will allow to set colors to every section more precisely.

Following are described the different views of the application that can be customized, including the associated resources:

#### Action bar

The action bar is present in all the activities, and offers back navigation, context information and options menu.

Wakup Activities uses two different customizable Appbars:

- The first ActionBar is used for the main (or root) activity
  
![](http://i.imgur.com/btq2Rq9.png)
  
  That includes a navigation bar:
  
![](http://i.imgur.com/jh6Sz22.png)

- The second ActionBar is displayed in the rest of the application sections

![](http://i.imgur.com/GNV07MH.png)

It is possible to customize the colors of the App bar...

```xml
    <!-- Colors -->
    <color name="wk_appbar_bg">@color/wk_primary</color>
    <color name="wk_appbar_text">@color/wk_secondary_reverse</color>
    <!-- Icon -->
    <drawable name="wk_actionbar_back">@drawable/wk_ic_nav_back</drawable>
```

... or the appeareance by directly overriding the layout `wk_toolbar.xml` or `wk_toolbar_root.xml`:

##### Include logo

In the following example we customize `wk_toolbar_root.xml` to include a centered ImageView that will display a logo only on root activity:
   
```xml
<?xml version="1.0" encoding="utf-8"?>
<android.support.v7.widget.Toolbar
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/toolbar"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:background="@color/wk_appbar_bg"
    app:popupTheme="@style/WakupTheme.PopupOverlay"
    app:theme="@style/WakupTheme.AppBarOverlay"
    app:layout_scrollFlags="scroll|enterAlways">

    <!-- ProgressBar shown on wk_appbar.xml while loading -->
    <ProgressBar
        android:id="@+id/progress_spinner"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_gravity="end"
        android:indeterminate="true"
        android:visibility="gone" />

    <!-- ImageView with logo centered -->
    <ImageView
        android:layout_width="wrap_content"
        android:layout_height="match_parent"
        android:layout_gravity="center"
        android:paddingTop="5dp"
        android:paddingBottom="5dp"
        android:src="@drawable/wk_actionbar_logo"/>

</android.support.v7.widget.Toolbar>
    
```

![](http://i.imgur.com/72p2G1r.png)

To hide title use empty Strings in the [activity titles](#strings)</a>.

![](http://i.imgur.com/VO0GI1D.png)

#### Navigation bar

![](http://i.imgur.com/jh6Sz22.png)

Throug the following resources it is easy to change icons or color of the navigation bar:

```xml
<!-- Colors -->
<color name="wk_navbar_bg">@color/wk_primary</color>
<color name="wk_navbar_pressed">@color/wk_primary_pressed</color>
<color name="wk_navbar_text">@color/wk_primary_reverse</color>
<color name="wk_navbar_divider">@color/wk_primary_reverse</color>
<!-- Icons -->
<drawable name="wk_nav_big_offer">@drawable/wk_ic_nav_big_offer</drawable>
<drawable name="wk_nav_map">@drawable/wk_ic_nav_map</drawable>
<drawable name="wk_nav_my_offers">@drawable/wk_ic_nav_my_offers</drawable>
```

If a deeper customization is required, it is possible to override `wk_root_navbar.xml` layout.

#### Offer item

![](http://i.imgur.com/HbfrxvAm.png)

```xml
<!-- Offer Item -->
<color name="wk_offer_list_item_bg">@color/wk_white</color>
<color name="wk_offer_short_desc">@color/wk_secondary_reverse</color>
<color name="wk_offer_short_desc_bg">@color/wk_secondary</color>
<!-- Icons -->
<drawable name="wk_offer_expiration">@drawable/wk_ic_expiration</drawable>
<drawable name="wk_offer_location">@drawable/wk_ic_location</drawable>
```
#### Offer detail

[![](http://i.imgur.com/WvISmNbm.png)](http://i.imgur.com/WvISmNb.png)

```xml
<!-- Colors -->
<color name="wk_store_offers">@color/wk_secondary_reverse</color>
<color name="wk_store_offers_bg">@color/wk_secondary</color>
<color name="wk_store_offers_bg_pressed">@color/wk_secondary_pressed</color>
<!-- Tag button -->
<color name="wk_tag_active_bg">@color/wk_primary</color>
<color name="wk_tag_pressed_bg">@color/wk_primary_pressed</color>
<color name="wk_tag_active_border">@color/wk_primary_pressed</color>
<color name="wk_tag_pressed_border">@color/wk_primary_pressed</color>
<color name="wk_tag_text">@color/wk_white</color>
```

#### Offer actions

![](http://i.imgur.com/cGItnT0m.png)

Action buttons used for offer actions and search category filters can also be customized in two steps.

First step will keep the same color for all the elements (background, icon and text) of the button:

```xml
<!-- Colors -->
<color name="wk_action_active">@color/wk_primary</color>
<color name="wk_action_pressed">@color/wk_white</color>
<color name="wk_action_inactive">@color/wk_light_text</color>
<!-- Icons -->
<drawable name="wk_action_save">@drawable/wk_ic_btn_save</drawable>
<drawable name="wk_action_share">@drawable/wk_ic_btn_share</drawable>
<drawable name="wk_action_web">@drawable/wk_ic_btn_website</drawable>
<drawable name="wk_action_locate">@drawable/wk_ic_btn_location</drawable>
<drawable name="wk_action_background">@drawable/wk_action_button</drawable>
```
If a more thorough  customization is needed it is possible to go a level deeper and customize the colors of every element separately:

```xml
<!-- Deep Customization Colors -->
<!-- Background -->
<color name="wk_action_active_bg">@color/wk_action_active</color>   
<color name="wk_action_pressed_bg">@color/wk_action_active</color>
<color name="wk_action_inactive_bg">@color/wk_action_inactive</color>
<!-- Icon -->
<color name="wk_action_active_icon">@color/wk_action_active</color>
<color name="wk_action_pressed_icon">@color/wk_action_pressed</color>
<color name="wk_action_inactive_icon">@color/wk_action_inactive</color>
<!-- Text -->
<color name="wk_action_active_text">@color/wk_action_active</color>
<color name="wk_action_pressed_text">@color/wk_action_pressed</color>
<color name="wk_action_inactive_text">@color/wk_action_inactive</color>
```

To change the default circle background, it would be required to override the default drawable by a customized one:

```xml
<!-- Action button background -->
<drawable name="wk_action_background">@drawable/wk_action_button</drawable>
``` 

#### Search view

This view allows users to find places, brands or tags and filter results using categories.

[![](http://i.imgur.com/SnlkUYAm.png)](http://i.imgur.com/SnlkUYA.png)

To customize texts, colors or icons

```xml
<!-- Colors -->
<color name="wk_search_box_text">#F1EAF2</color>
<color name="wk_search_box_cursor">#F1EAF2</color>
<color name="wk_search_header_bg">#F6F6F6</color>
<color name="wk_search_list_bg">@color/wk_white</color>
<color name="wk_search_icon">@color/wk_light_text</color>
<!-- Category colors -->
<color name="wk_search_cat_leisure">@color/wk_action_active</color>
<color name="wk_search_cat_restaurants">@color/wk_action_active</color>
<color name="wk_search_cat_services">@color/wk_action_active</color>
<color name="wk_search_cat_shopping">@color/wk_action_active</color>
<!-- Category icons -->
<drawable name="wk_cat_leisure">@drawable/wk_ic_btn_leisure</drawable>
<drawable name="wk_cat_restaurants">@drawable/wk_ic_btn_restaurants</drawable>
<drawable name="wk_cat_services">@drawable/wk_ic_btn_services</drawable>
<drawable name="wk_cat_shopping">@drawable/wk_ic_btn_shopping</drawable>
<!-- Result item icons -->
<drawable name="wk_search_brand">@drawable/wk_ic_search_brand</drawable>
<drawable name="wk_search_geo">@drawable/wk_ic_search_geo</drawable>
<drawable name="wk_search_tag">@drawable/wk_ic_search_tag</drawable>
```

It is also possible to customize the filters that can be selected by the users and applied in the search results. This will allow to show only results of offers that match any of the tags contained in the selected categories.

To change the default values, it is needed to include an array of Categories with the desired options in the WakupOptions object of the setup method:

```java
List<Category> categories = Arrays.asList(
        new Category(R.string.cat_book, R.drawable.cat_book, "books", "ebooks"),
        new Category(R.string.cat_leisure, R.drawable.cat_leisure, "bowling", "cinema"),
        new Category(R.string.cat_tech, R.drawable.cat_tech, "technology"),
        new Category(R.string.cat_promo, R.drawable.promo, "offers", "promotions"),
        new Category(R.string.cat_partners, R.drawable.partners, "partners")
);

Wakup.instance(this).launch(
        new WakupOptions("WAKUP_API_KEY").
                categories(categories)
);

```

#### Offers map

[![](http://i.imgur.com/Rnz2cEfm.png)](http://i.imgur.com/Rnz2cEf.png)

The icon for the Map Marker that represents an offer location will change depending on the tags associated with the displayed offer.

Default markers will show different icons for offers with the following tags:


You can change the icon used for the default categories by updating its drawable reference:

```xml
<!-- Icons (colored) -->
<drawable name="wk_pin_unknown">@drawable/wk_ic_pin_unknown</drawable>
<drawable name="wk_pin_leisure">@drawable/wk_ic_pin_leisure</drawable>
<drawable name="wk_pin_restaurants">@drawable/wk_ic_pin_restaurant</drawable>
<drawable name="wk_pin_services">@drawable/wk_ic_pin_services</drawable>
<drawable name="wk_pin_shopping">@drawable/wk_ic_pin_shopping</drawable>
```

It is also possible to customize the tag categories that determine the used Map Marker for each case.

To do so, include an Collection of MapMarker objects in the WakupOptions object created for the setup method:

```java
List<MapMarker> mapMarkers = Arrays.asList(
        new MapMarker(R.drawable.pin_books, "books", "ebooks"),
        new MapMarker(R.drawable.pin_leisure, "leisure"),
        new MapMarker(R.drawable.pin_tech, "technology"),
        new MapMarker(R.drawable.pin_promo, "offers", "promotions"),
        new MapMarker(R.drawable.pin_restaurants, "restaurants"),
        new MapMarker(R.drawable.pin_default)
);

// Wakup
Wakup.instance(this).launch(
        new WakupOptions("WAKUP_API_KEY").
                mapMarkers(mapMarkers)
);
```

Each Map Marker will contain the associated icon and an array of offer tags that will be represented by it. To include a default icon for offers that does not match any of the tags of the another markers, you can use the constructor with no tags.

#### Empty result views

![](http://i.imgur.com/zZQFaXCm.png) ![](http://i.imgur.com/p8okjk4m.png?1)

```xml
<!-- Colors -->
<color name="wk_no_results_text">#8F8F8F</color>
<!-- Icons -->
<drawable name="wk_empty_offers">@drawable/wk_ic_warning</drawable>
<drawable name="wk_empty_my_offers">@drawable/wk_ic_saved_offers</drawable>
```

#### Common

```xml
<!-- Activity -->
<color name="wk_background">#D5D5D5</color>

<!-- Text color -->
<color name="wk_main_text">#505050</color>
<color name="wk_bold_text">#393939</color>
<color name="wk_light_text">#8F8F8F</color>
```

# Dependencies

The following dependencies are used in the project:

* [Async Http](http://loopj.com/android-async-http/): Library for asynchronous requests
* [Gson](http://code.google.com/p/google-gson/): Parse and serialize JSON
* [Calligraphy](https://github.com/chrisjenx/Calligraphy): Allows setting custom typeface to Text Views
* [Universal-Image-Loader](https://github.com/nostra13/Android-Universal-Image-Loader): Library to load and cache images by URL
* [Autofit TextView](https://github.com/grantland/android-autofittextview): Auto shrink large texts to adapt to available space
* [Carousel View](https://github.com/sayyam/carouselview): Horizontal view pager with page indicator
* [Flow layout](https://github.com/ApmeM/android-flowlayout): Linear layout, that wrap its content to the next line if there is no space in the current line.