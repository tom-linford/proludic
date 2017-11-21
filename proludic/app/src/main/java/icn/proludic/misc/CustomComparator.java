package icn.proludic.misc;

import java.util.Comparator;

import icn.proludic.models.FriendsModel;

/**
 * Author:  Bradley Wilson
 * Date: 29/06/2017
 * Package: icn.proludic.misc
 * Project Name: proludic
 */

public class CustomComparator implements Comparator<FriendsModel> {
    @Override
    public int compare(FriendsModel f1, FriendsModel f2) {
        return f1.getUsername().compareTo(f2.getUsername());
    }
}
