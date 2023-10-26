package com.petesitemmanager.pim.service.dto;

public class ProfileDto {

    public ProfileDto() {

    }

    private CharacterDto hunter;

    private CharacterDto titan;

    private CharacterDto warlock;

    private VaultDto vault;

    public CharacterDto getHunter() {
        return hunter;
    }

    public void setHunter(CharacterDto hunter) {
        this.hunter = hunter;
    }

    public CharacterDto getTitan() {
        return titan;
    }

    public void setTitan(CharacterDto titan) {
        this.titan = titan;
    }

    public CharacterDto getWarlock() {
        return warlock;
    }

    public void setWarlock(CharacterDto warlock) {
        this.warlock = warlock;
    }

    public VaultDto getVault() {
        return vault;
    }

    public void setVault(VaultDto vault) {
        this.vault = vault;
    }

}
