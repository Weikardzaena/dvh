package org.swistowski.dvh.util;

import android.content.Context;
import android.util.Log;
import android.widget.BaseAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.swistowski.dvh.R;
import org.swistowski.dvh.db.DB;
import org.swistowski.dvh.models.Character;
import org.swistowski.dvh.models.Item;
import org.swistowski.dvh.models.Membership;
import org.swistowski.dvh.models.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Data implements Serializable {
    public static final String VAULT_ID = "VAULT";
    private static final String LOG_TAG = "Database";
    private static final Data ourInstance = new Data();
    private User mUser;
    private Membership mMembership;
    private List<Character> mCharacters;
    private Map<String, List<Item>> items = new HashMap<>();
    private Map<Item, String> itemsOwners = new HashMap<>();
    private Set<String> bucketNames = new HashSet<>();
    private final Set<BaseAdapter> registeredAdapters = new HashSet<>();

    private LinkedHashMap<Integer, Boolean> mBucketFilters;
    private static final LinkedHashMap<Integer, Boolean> BUCKET_FILTERS;

    static {
        BUCKET_FILTERS = new LinkedHashMap<>();

        BUCKET_FILTERS.put(R.string.primary_weapons_bucket, Boolean.FALSE);
        BUCKET_FILTERS.put(R.string.special_weapons_bucket, Boolean.FALSE);
        BUCKET_FILTERS.put(R.string.heavy_weapons_bucket, Boolean.FALSE);
        BUCKET_FILTERS.put(R.string.helmets_bucket, Boolean.FALSE);
        BUCKET_FILTERS.put(R.string.chest_armors_bucket, Boolean.FALSE);
        BUCKET_FILTERS.put(R.string.gauntlets_bucket, Boolean.FALSE);
        BUCKET_FILTERS.put(R.string.leg_armor_bucket, Boolean.FALSE);
        BUCKET_FILTERS.put(R.string.class_armor_bucket, Boolean.FALSE);
        BUCKET_FILTERS.put(R.string.materials_bucket, Boolean.FALSE);
        BUCKET_FILTERS.put(R.string.consumables_bucket, Boolean.FALSE);
        BUCKET_FILTERS.put(R.string.emblems_bucket, Boolean.FALSE);
        BUCKET_FILTERS.put(R.string.ghosts_bucket, Boolean.FALSE);
        BUCKET_FILTERS.put(R.string.shaders_bucket, Boolean.FALSE);
        BUCKET_FILTERS.put(R.string.ships_bucket, Boolean.FALSE);
        BUCKET_FILTERS.put(R.string.vehicle_bucket, Boolean.FALSE);
    }

    private static final LinkedHashMap<String, Integer> BUCKET_FILTERS_LABELS;

    static {
        BUCKET_FILTERS_LABELS = new LinkedHashMap<>();
        BUCKET_FILTERS_LABELS.put("Primary Weapons", R.string.primary_weapons_bucket);
        BUCKET_FILTERS_LABELS.put("Special Weapons", R.string.special_weapons_bucket);
        BUCKET_FILTERS_LABELS.put("Heavy Weapons", R.string.heavy_weapons_bucket);
        BUCKET_FILTERS_LABELS.put("Helmet", R.string.helmets_bucket);
        BUCKET_FILTERS_LABELS.put("Chest Armor", R.string.chest_armors_bucket);
        BUCKET_FILTERS_LABELS.put("Gauntlets", R.string.gauntlets_bucket);
        BUCKET_FILTERS_LABELS.put("Leg Armor", R.string.leg_armor_bucket);
        BUCKET_FILTERS_LABELS.put("Class Armor", R.string.class_armor_bucket);
        BUCKET_FILTERS_LABELS.put("Materials", R.string.materials_bucket);
        BUCKET_FILTERS_LABELS.put("Consumables", R.string.consumables_bucket);
        BUCKET_FILTERS_LABELS.put("Emblems", R.string.emblems_bucket);
        BUCKET_FILTERS_LABELS.put("Ghost", R.string.ghosts_bucket);
        BUCKET_FILTERS_LABELS.put("Shaders", R.string.shaders_bucket);
        BUCKET_FILTERS_LABELS.put("Ships", R.string.ships_bucket);
        BUCKET_FILTERS_LABELS.put("Vehicle", R.string.vehicle_bucket);
    }

    private LinkedHashMap<Integer, Boolean> mDamageFilters;
    private static final LinkedHashMap<Integer, Boolean> DAMAGE_FILTERS;

    static {
        DAMAGE_FILTERS = new LinkedHashMap<>();
        DAMAGE_FILTERS.put(R.string.arc_damage, Boolean.FALSE);
        DAMAGE_FILTERS.put(R.string.solar_damage, Boolean.FALSE);
        DAMAGE_FILTERS.put(R.string.void_damage, Boolean.FALSE);
    }

    private static final LinkedHashMap<String, Integer> DAMAGE_FILTERS_LABELS;

    static {
        DAMAGE_FILTERS_LABELS = new LinkedHashMap<>();
        DAMAGE_FILTERS_LABELS.put("Arc", R.string.arc_damage);
        DAMAGE_FILTERS_LABELS.put("Solar", R.string.solar_damage);
        DAMAGE_FILTERS_LABELS.put("Void", R.string.void_damage);
    }

    private HashMap<String, Set<Long>> mLabels = new HashMap<>();

    private LinkedHashMap<Integer, Boolean> mCompletedFilters;

    /*
    private static final String IS_COMPLETED = "is completed";
    private static final String IS_NOT_COMPLETED = "is not completed";
    */

    private static final LinkedHashMap<Integer, Boolean> COMPLETED_FILTERS;

    static {
        COMPLETED_FILTERS = new LinkedHashMap<>();
        COMPLETED_FILTERS.put(R.string.is_completed, Boolean.FALSE);
        COMPLETED_FILTERS.put(R.string.is_not_completed, Boolean.FALSE);
    }

    private static final LinkedHashMap<Integer, String> COMPLETED_FILTERS_LABELS;

    static {
        COMPLETED_FILTERS_LABELS = new LinkedHashMap<>();

    }

    private DB mDb;
    private Context context;

    public LinkedHashMap<Integer, Boolean> getBucketFilters() {
        if (mBucketFilters == null) {
            mBucketFilters = new LinkedHashMap<>();
            for (Map.Entry<Integer, Boolean> entry : BUCKET_FILTERS.entrySet()) {
                mBucketFilters.put(entry.getKey(), entry.getValue());
            }
        }
        return mBucketFilters;
    }

    public LinkedHashMap<Integer, Boolean> getDamageFilters() {
        if (mDamageFilters == null) {
            mDamageFilters = new LinkedHashMap<>();
            for (Map.Entry<Integer, Boolean> entry : DAMAGE_FILTERS.entrySet()) {
                mDamageFilters.put(entry.getKey(), entry.getValue());
            }
        }
        return mDamageFilters;
    }

    public LinkedHashMap<Integer, Boolean> getCompletedFilters() {
        if (mCompletedFilters == null) {
            mCompletedFilters = new LinkedHashMap<>();
            for (Map.Entry<Integer, Boolean> entry : COMPLETED_FILTERS.entrySet()) {
                mCompletedFilters.put(entry.getKey(), entry.getValue());
            }
        }
        return mCompletedFilters;
    }

    private boolean mIsLoading = false;

    private String mFilterText = "";

    private Data() {
    }

    public static Data getInstance() {
        return ourInstance;
    }


    public User loadUserFromJson(JSONObject json) throws JSONException {
        mUser = User.fromJson(json);
        return mUser;
    }

    public User getUser() {
        return mUser;
    }

    public Membership loadMembershipFromJson(JSONObject json) {
        this.mMembership = Membership.fromJson(json);
        return mMembership;
    }

    public void loadCharactersFromJson(JSONArray jsonArray) throws JSONException {
        mCharacters = Character.collectionFromJson(jsonArray);
    }

    public Membership getMembership() {
        return mMembership;
    }

    public List<Character> getCharacters() {
        return mCharacters;
    }

    public Map<String, List<Item>> getItems() {
        return items;
    }

    public void putItems(String id, List<Item> items) {
        this.items.put(id, items);
        for (Item item : items) {
            this.itemsOwners.put(item, id);
            this.bucketNames.add(item.getBucketName());
        }
    }

    public List<Item> getAllItems() {
        List<Item> allItems = new ArrayList<Item>();

        for (Map.Entry<String, List<Item>> entry : items.entrySet()) {
            for (Item i : entry.getValue()) {
                if (i.isVisible() && isVisible(i))
                    allItems.add(i);
            }
        }
        Collections.sort(allItems);
        return allItems;
    }

    public void clean() {
        mUser = null;
        mMembership = null;
        mCharacters = null;
        cleanItems();
    }

    void cleanItems() {
        items = new HashMap<>();
        itemsOwners = new HashMap<>();
    }

    private boolean isVisible(Item item) {
        return filterByBucket(item) && filterByDamage(item) && filterByCompleted(item) && filterByText(item);
        //return true;
    }

    private boolean filterByCompleted(Item item) {
        if (getCompletedFilters().containsValue(Boolean.TRUE)) {
            return getCompletedFilters().get(R.string.is_completed).booleanValue() && item.getIsCompleted() ||
                    (getCompletedFilters().get(R.string.is_not_completed).booleanValue() && !item.getIsCompleted());
        }
        return true;
    }

    private boolean filterByDamage(Item item) {
        if (getDamageFilters().containsValue(Boolean.TRUE)) {
            return getDamageFilters().containsKey(DAMAGE_FILTERS_LABELS.get(item.getDamageTypeName())) && getDamageFilters().get(DAMAGE_FILTERS_LABELS.get(item.getDamageTypeName()));
        } else {
            return true;
        }
    }

    private boolean filterByBucket(Item item) {
        if (getBucketFilters().values().contains(Boolean.TRUE)) {
            return getBucketFilters().get(BUCKET_FILTERS_LABELS.get(item.getBucketName()));
        } else {
            return true;
        }
    }

    private boolean filterByText(Item item) {
        if (!mFilterText.equals("")) {
            return item.getName().toLowerCase().contains(mFilterText.toLowerCase());
        }
        return true;
    }

    public List<Item> notForItems(String key) {
        List<Item> allItems = new ArrayList<Item>();
        for (Map.Entry<String, List<Item>> entry : items.entrySet()) {
            if (!entry.getKey().equals(key)) {
                for (Item i : entry.getValue()) {
                    if (i.isVisible() && isVisible(i))
                        allItems.add(i);
                }
            }
        }
        Collections.sort(allItems);
        return allItems;
    }


    public ArrayList<Item> getItemsFiltered(String id) {
        ArrayList<Item> allItems = new ArrayList<Item>();
        for (Item item : items.get(id)) {
            if (item.isVisible() && isVisible(item))
                allItems.add(item);
        }
        Collections.sort(allItems);
        return allItems;
    }

    public void cleanCharacters() {
        mCharacters = null;
        cleanItems();
    }

    public String getItemOwner(Item item) {
        return itemsOwners.get(item);
    }

    public String getItemOwnerName(Item item) {
        String owner = getItemOwner(item);
        if (owner == null) {
            return "None";
        }
        if (owner.equals(VAULT_ID)) {
            return "Vault";
        }
        for (Character character : mCharacters) {
            if (character.getId().equals(owner)) {
                return character.toString();
            }
        }
        return "None";
    }

    public void changeOwner(Item item, String target) {
        String owner = getItemOwner(item);
        items.get(owner).remove(item);
        items.get(target).add(item);
        itemsOwners.put(item, target);

        notifyItemsChanged();
    }

    public void notifyItemsChanged() {
        for (BaseAdapter adapter : registeredAdapters) {
            adapter.notifyDataSetChanged();
        }
    }

    public void registerItemAdapter(BaseAdapter adapter) {
        Log.v(LOG_TAG, "Adding adapter: " + adapter.toString());
        registeredAdapters.add(adapter);
    }

    public void unregisterItemAdapter(BaseAdapter adapter) {
        Log.v(LOG_TAG, "Removing adapter: " + adapter.toString());
        registeredAdapters.remove(adapter);
    }

    public void setBucketFilters(Set<Integer> bucketFilters) {
        for (Integer key : mBucketFilters.keySet()) {
            if (bucketFilters.contains(key)) {
                mBucketFilters.put(key, Boolean.TRUE);
            } else {
                mBucketFilters.put(key, Boolean.FALSE);
            }
        }
        notifyItemsChanged();
    }

    public void setFilterText(String filterText) {
        this.mFilterText = filterText;
        notifyItemsChanged();
    }

    public synchronized void setIsLoading(boolean isLoading) {
        mIsLoading = isLoading;
    }

    public boolean getIsLoading() {
        return mIsLoading;
    }

    public DB getDb() {
        if (mDb == null) {
            mDb = new DB(getContext());
        }
        return mDb;
    }

    private Set<Long> getLabelItems(String label){
        Set<Long> labels = mLabels.get(label);
        if(labels == null){
            labels = getDb().labelItems(label);
            mLabels.put(label, labels);
        }
        return labels;
    }

    public void addLabel(long item_id, String label) {
        getDb().addLabel(item_id, label);
        getLabelItems(label).add(item_id);
    }

    public void deleteLabel(long item_id, String label) {
        getDb().deleteLabel(item_id, label);
        getLabelItems(label).remove(item_id);
    }

    public boolean hasLabel(long item_id, String label) {
        return getLabelItems(label).contains(item_id);
    }

    public Context getContext() {
        return context;
    }



    public void setContext(Context context) {
        this.context = context;

    }

        /*
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void load(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            FileInputStream fis = null;
            try {
                fis = context.openFileInput("database");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return;
            }
            try {
                try (ObjectInputStream is = new ObjectInputStream(fis)) {
                    try {
                        ourInstance = (Database) is.readObject();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

        @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void save(Context context) throws IOException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try (FileOutputStream fos = context.openFileOutput("database", Context.MODE_PRIVATE)) {
                try (ObjectOutputStream os = new ObjectOutputStream(fos)) {
                    os.writeObject(ourInstance);
                    os.close();
                }
                fos.close();
            }
        }
    }
    */

}
