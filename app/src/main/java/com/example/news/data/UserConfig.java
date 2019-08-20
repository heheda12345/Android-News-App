package com.example.news.data;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class UserConfig {
    public static class Section {
        private String mSectionName;
        private int mPosition;
        Section(String sectionName, int position) {
            mSectionName = sectionName;
            mPosition = position;
        }

        public String getSectionName() {
            return mSectionName;
        }

        public int getPosition() {
            return mPosition;
        }
    }

    /* Index of this list <-> Position at the tabs*/
    private List<Integer> selectSectionsIndices;
    private List<Integer> unselectedSectionsIndices;

    private static UserConfig instance = new UserConfig();

    private UserConfig() {
        selectSectionsIndices = new ArrayList<>();
        unselectedSectionsIndices = new ArrayList<>();
        for (int i = 0; i < ConstantValues.ALL_SECTIONS.length; i += 2) {
            selectSectionsIndices.add(i);
        }
        for (int i = 1; i < ConstantValues.ALL_SECTIONS.length; i += 2) {
            unselectedSectionsIndices.add(i);
        }
    }

    public static UserConfig getInstance() {
        return instance;
    }

    public List<Section> getAllSelectSections() {
        List<Section> sL = new ArrayList<>();
        for (int position = 0; position < selectSectionsIndices.size(); ++position) {
            sL.add(new Section(ConstantValues.ALL_SECTIONS[selectSectionsIndices.get(position)], position));
        }
        return sL;
    }

    public List<Section> getAllUnselectedSections() {
        List<Section> sL = new ArrayList<>();
        for (int position = 0; position < unselectedSectionsIndices.size(); ++position) {
            sL.add(new Section(ConstantValues.ALL_SECTIONS[unselectedSectionsIndices.get(position)], position));
        }
        return sL;
    }

    public Section getSection(int position) {
        int sectionIndex = selectSectionsIndices.get(position);
        return new Section(ConstantValues.ALL_SECTIONS[sectionIndex], position);
    }

    public Section getUnSetion(int position) {
        int sectionIndex = unselectedSectionsIndices.get(position);
        return new Section(ConstantValues.ALL_SECTIONS[sectionIndex], position);
    }

    public int getSectionNum() {
        return selectSectionsIndices.size();
    }

    public int getUnSectionNum() { return unselectedSectionsIndices.size(); }

    public void addSection(int pos) {
        int id = unselectedSectionsIndices.get(pos);
        unselectedSectionsIndices.remove(pos);
        selectSectionsIndices.add(id);

    }

    public void removeSection(int pos) {
        int id = selectSectionsIndices.get(pos);
        selectSectionsIndices.remove(pos);
        unselectedSectionsIndices.add(id);
    }
}
