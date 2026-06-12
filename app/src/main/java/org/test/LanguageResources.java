package org.test;

public class LanguageResources {

    public LanguageResources() {
    }

    public static String getAreYouSure() {
        return "确定退出吗？";
    }

    public static String getFontPreloadChars() {
        return "0123456789 /.-+:%"
                + getAreYouSure()
                + getBack()
                + getBlocking()
                + getEasy()
                + getEnemies()
                + getHard()
                + getInstructions()
                + getLevel1Title()
                + getLevel2Title()
                + getLevel3Title()
                + getLevel4Title()
                + getLevel5Title()
                + getLevel6Title()
                + getLevel7Title()
                + getLevel8Title()
                + getLevel9Title()
                + getLoseHeader()
                + getLosePar1()
                + getMainMenu()
                + getMedium()
                + getMenu()
                + getMonsterInfoPar1()
                + getMonsterInfoPar2()
                + getMonsterInfoPar3()
                + getMonsterInfoPar4()
                + getMonsterInfoPar5()
                + getMonsterInfoPar6()
                + getNext()
                + getNextWave()
                + getNo()
                + getPower()
                + getAttackInterval()
                + getMaxHealth()
                + getRestart()
                + getResume()
                + getSell()
                + getStart()
                + getTowerInfoPar1()
                + getTowerInfoPar2()
                + getTowerInfoPar3()
                + getTowerInfoPar4()
                + getTowers()
                + getUpgrade()
                + getGem()
                + getWinHeader()
                + getWinPar1()
                + getYes();
    }

    public static String getBack() {
        return "返回";
    }

    public static String getBlocking() {
        return "阻断了";
    }

    public static String getEasy() {
        return "简单模式";
    }

    public static String getEnemies() {
        return "偷袭者";
    }

    public static String getHard() {
        return "困难模式";
    }

    public static String getInstructions() {
        return "游戏说明";
    }

    public static String getLevel1Title() {
        return "初出茅庐";
    }

    public static String getLevel2Title() {
        return "无名小卒";
    }

    public static String getLevel3Title() {
        return "锋芒乍现";
    }

    public static String getLevel4Title() {
        return "小试牛刀";
    }

    public static String getLevel5Title() {
        return "英雄初阵";
    }

    public static String getLevel6Title() {
        return "建功立业";
    }

    public static String getLevel7Title() {
        return "名扬四海";
    }

    public static String getLevel8Title() {
        return "一代枭雄";
    }

    public static String getLevel9Title() {
        return "纵横天下";
    }

    public static String getLevelTitle(Difficulty difficulty, int level) {
        switch (difficulty.getValue() * 3 + level) {
        case 1:
            return getLevel1Title();
        case 2:
            return getLevel2Title();
        case 3:
            return getLevel3Title();
        case 4:
            return getLevel4Title();
        case 5:
            return getLevel5Title();
        case 6:
            return getLevel6Title();
        case 7:
            return getLevel7Title();
        case 8:
            return getLevel8Title();
        case 9:
            return getLevel9Title();
        default:
            return getLevel1Title();
        }
    }

    public static String getLoseHeader() {
        return "遗憾";
    }

    public static String getLosePar1() {
        return "再接再厉";
    }

    public static String getMainMenu() {
        return "主菜单";
    }

    public static String getMedium() {
        return "常规模式";
    }

    public static String getMenu() {
        return "菜单";
    }

    public static String getMonsterInfoPar1() {
        return "山鸡怪";
    }

    public static String getMonsterInfoPar2() {
        return "蘑菇怪";
    }

    public static String getMonsterInfoPar3() {
        return "盾牌怪";
    }

    public static String getMonsterInfoPar4() {
        return "彩鸦怪";
    }

    public static String getMonsterInfoPar5() {
        return "草头怪";
    }

    public static String getMonsterInfoPar6() {
        return "碎石怪";
    }

    public static String getNext() {
        return "下波";
    }

    public static String getNextWave() {
        return "下一波";
    }

    public static String getNo() {
        return "取消";
    }

    public static String getof() {
        return "/";
    }

    public static String getPower() {
        return "攻击";
    }

    public static String getAttackInterval() {
        return "攻速";
    }

    public static String getMaxHealth() {
        return "生命上限";
    }

    public static String getRestart() {
        return "重开";
    }

    public static String getResume() {
        return "继续";
    }

    public static String getSell() {
        return "出售";
    }

    public static String getStart() {
        return "开始";
    }

    public static String getTowerInfoPar1() {
        return "飞斧塔";
    }

    public static String getTowerInfoPar2() {
        return "飞矛塔";
    }

    public static String getTowerInfoPar3() {
        return "防空塔";
    }

    public static String getTowerInfoPar4() {
        return "撼地塔";
    }

    public static String getTowers() {
        return "防御塔";
    }

    public static String getUpgrade() {
        return "升级";
    }

    public static String getGem() {
        return "宝石";
    }

    public static String getWave() {
        return "";
    }

    public static String getWinHeader() {
        return "恭喜";
    }

    public static String getWinPar1() {
        return "通过了";
    }

    public static String getYes() {
        return "确定";
    }
}
