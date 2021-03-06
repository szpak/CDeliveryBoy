package info.solidsoft.gradle.cdeliveryboy.infra

interface ReleaseVersionOverrider {

    /**
     * Overrides release version.
     *
     * Expects an one of supported incrementers or a Semantic Versioning compilant version number.
     *
     * @param overriddenVersion overridden version to use
     */
    void overrideReleaseVersion(String overriddenVersion)
}
