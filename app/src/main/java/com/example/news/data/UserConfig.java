package com.example.news.data;

import java.util.ArrayList;
import java.util.List;

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

    public static class TTS { // 语音合成的配置
        private String voicer = "xiaoyan";

        public String getVoicer() {
            return voicer;
        }

        public void setVoicer(String voicer) {
            this.voicer = voicer;
        }
    }

    /* Index of this list <-> Position at the tabs*/
    private List<Integer> selectSectionsIndices;
    private TTS tts;

    private static UserConfig instance = new UserConfig();

    private UserConfig() {
        selectSectionsIndices = new ArrayList<>();
        for (int i = 0; i < ConstantValues.ALL_SECTIONS.length; ++i) {
            selectSectionsIndices.add(i);
        }
        tts = new TTS();
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

    public Section getSection(int position) {
        int sectionIndex = selectSectionsIndices.get(position);
        return new Section(ConstantValues.ALL_SECTIONS[sectionIndex], position);
    }

    public int getSectionNum() {
        return selectSectionsIndices.size();
    }

    public String getTTSVoicer() { return tts.getVoicer(); }
    public void setTTSVoicer(String voicer) { tts.setVoicer(voicer);}
}
