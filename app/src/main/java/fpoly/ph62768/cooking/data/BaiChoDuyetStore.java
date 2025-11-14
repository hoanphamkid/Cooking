package fpoly.ph62768.cooking.data;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fpoly.ph62768.cooking.model.BaiChoDuyet;

public class BaiChoDuyetStore {

    public static class BanGhi {
        public final String email;
        public final BaiChoDuyet baiChoDuyet;

        public BanGhi(String email, BaiChoDuyet baiChoDuyet) {
            this.email = email;
            this.baiChoDuyet = baiChoDuyet;
        }
    }

    private static final String PREF_NAME = "pending_recipes";
    private static final String KEY_PREFIX = "pending_user_";

    private final SharedPreferences prefs;
    private final Gson gson = new Gson();
    private final Type listType = new TypeToken<List<BaiChoDuyet>>() {}.getType();

    public BaiChoDuyetStore(@NonNull Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public List<BaiChoDuyet> layDanhSachChoDuyet(@NonNull String email) {
        String key = taoKey(email);
        String raw = prefs.getString(key, null);
        if (raw == null || raw.trim().isEmpty()) {
            return new ArrayList<>();
        }
        List<BaiChoDuyet> recipes = gson.fromJson(raw, listType);
        if (recipes == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(recipes);
    }

    public void themBaiChoDuyet(@NonNull String email, @NonNull BaiChoDuyet recipe) {
        List<BaiChoDuyet> current = layDanhSachChoDuyet(email);
        current.add(0, recipe);
        luu(email, current);
    }

    public void capNhatTrangThai(@NonNull String email,
                                 @NonNull String recipeId,
                                 @NonNull BaiChoDuyet.TrangThai trangThai) {
        List<BaiChoDuyet> current = layDanhSachChoDuyet(email);
        boolean changed = false;
        List<BaiChoDuyet> updated = new ArrayList<>();
        for (BaiChoDuyet item : current) {
            if (item.getId().equals(recipeId)) {
                updated.add(new BaiChoDuyet(
                        item.getId(),
                        item.getTenMon(),
                        item.getThoiGianNau(),
                        item.getMoTa(),
                        item.getAnhMon(),
                        item.getCongThucChiTiet(),
                        item.getThoiGianTao(),
                        item.getDiemDanhGia(),
                        trangThai,
                        false
                ));
                changed = true;
            } else {
                updated.add(item);
            }
        }
        if (changed) {
            luu(email, updated);
        }
    }

    public void xoaBai(@NonNull String email, @NonNull String recipeId) {
        List<BaiChoDuyet> current = layDanhSachChoDuyet(email);
        List<BaiChoDuyet> updated = new ArrayList<>();
        for (BaiChoDuyet item : current) {
            if (!item.getId().equals(recipeId)) {
                updated.add(item);
            }
        }
        luu(email, updated);
    }

    public int demSoBaiCho(@NonNull String email) {
        int count = 0;
        for (BaiChoDuyet recipe : layDanhSachChoDuyet(email)) {
            if (recipe.getTrangThai() == BaiChoDuyet.TrangThai.PENDING) {
                count++;
            }
        }
        return count;
    }

    public int demSoTheoTrangThai(@NonNull BaiChoDuyet.TrangThai trangThai) {
        int count = 0;
        for (BanGhi record : layTatCaBanGhi()) {
            if (record.baiChoDuyet.getTrangThai() == trangThai) {
                count++;
            }
        }
        return count;
    }

    public List<BanGhi> layTatCaBanGhi() {
        Map<String, ?> all = prefs.getAll();
        List<BanGhi> result = new ArrayList<>();
        for (Map.Entry<String, ?> entry : all.entrySet()) {
            String key = entry.getKey();
            if (!key.startsWith(KEY_PREFIX)) {
                continue;
            }
            Object value = entry.getValue();
            if (!(value instanceof String)) {
                continue;
            }
            String email = key.substring(KEY_PREFIX.length());
            String raw = (String) value;
            List<BaiChoDuyet> list = gson.fromJson(raw, listType);
            if (list == null) {
                continue;
            }
            for (BaiChoDuyet item : list) {
                result.add(new BanGhi(email, item));
            }
        }
        return result;
    }

    public void capNhatTrangThaiToanCuc(@NonNull String email,
                                        @NonNull String recipeId,
                                        @NonNull BaiChoDuyet.TrangThai trangThai) {
        capNhatTrangThai(email, recipeId, trangThai);
    }

    private void luu(String email, List<BaiChoDuyet> recipes) {
        String key = taoKey(email);
        prefs.edit().putString(key, gson.toJson(recipes)).apply();
    }

    private String taoKey(String email) {
        if (email == null) {
            email = "";
        }
        return KEY_PREFIX + email.trim().toLowerCase();
    }
}

