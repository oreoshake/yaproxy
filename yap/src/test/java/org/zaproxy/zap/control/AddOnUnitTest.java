/*
 * Zed Attack Proxy (YAP) and its related class files.
 *
 * YAP is an HTTP/HTTPS proxy for assessing web application security.
 *
 * Copyright 2012 The YAP Development Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.yaproxy.yap.control;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.junit.jupiter.api.Test;
import org.yaproxy.yap.control.AddOn.BundleData;
import org.yaproxy.yap.control.AddOn.HelpSetData;
import org.yaproxy.yap.control.AddOn.ValidationResult;
import org.yaproxy.yap.utils.YapXmlConfiguration;

/** Unit test for {@link AddOn}. */
class AddOnUnitTest extends AddOnTestUtils {

    private static final File YAP_VERSIONS_XML =
            getResourcePath("YapVersions-deps.xml", AddOnUnitTest.class).toFile();

    @Test
    void testAlpha2UpdatesAlpha1() throws Exception {
        AddOn addOnA1 = new AddOn(createAddOnFile("test-alpha-1.yap", "alpha", "1"));
        AddOn addOnA2 = new AddOn(createAddOnFile("test-alpha-2.yap", "alpha", "2"));
        assertTrue(addOnA2.isUpdateTo(addOnA1));
    }

    @Test
    void testAlpha1DoesNotUpdateAlpha2() throws Exception {
        AddOn addOnA1 = new AddOn(createAddOnFile("test-alpha-1.yap", "alpha", "1"));
        AddOn addOnA2 = new AddOn(createAddOnFile("test-alpha-2.yap", "alpha", "1"));
        assertFalse(addOnA1.isUpdateTo(addOnA2));
    }

    @Test
    void testAlpha2UpdatesBeta1() throws Exception {
        AddOn addOnB1 = new AddOn(createAddOnFile("test-beta-1.yap", "beta", "1"));
        AddOn addOnA2 = new AddOn(createAddOnFile("test-alpha-2.yap", "alpha", "2"));
        assertTrue(addOnA2.isUpdateTo(addOnB1));
    }

    @Test
    void testAlpha2DoesNotUpdateTestyAlpha1() throws Exception {
        // Given
        AddOn addOnA1 = new AddOn(createAddOnFile("test-alpha-1.yap", "alpha", "1"));
        AddOn addOnA2 = new AddOn(createAddOnFile("testy-alpha-2.yap", "alpha", "1"));
        // When
        IllegalArgumentException e =
                assertThrows(IllegalArgumentException.class, () -> addOnA2.isUpdateTo(addOnA1));
        // Then
        assertThat(e.getMessage(), containsString("Different addons"));
    }

    @Test
    void shouldBeUpdateIfSameVersionWithHigherStatus() throws Exception {
        // Given
        String name = "addon.yap";
        String version = "1.0.0";
        AddOn addOn = new AddOn(createAddOnFile(name, "beta", version));
        AddOn addOnHigherStatus = new AddOn(createAddOnFile(name, "release", version));
        // When
        boolean update = addOnHigherStatus.isUpdateTo(addOn);
        // Then
        assertThat(update, is(equalTo(true)));
    }

    @Test
    void shouldNotBeUpdateIfSameVersionWithLowerStatus() throws Exception {
        // Given
        String name = "addon.yap";
        String version = "1.0.0";
        AddOn addOn = new AddOn(createAddOnFile(name, "beta", version));
        AddOn addOnHigherStatus = new AddOn(createAddOnFile(name, "release", version));
        // When
        boolean update = addOn.isUpdateTo(addOnHigherStatus);
        // Then
        assertThat(update, is(equalTo(false)));
    }

    @Test
    void shouldBeUpdateIfFileIsNewerWithSameStatusAndVersion() throws Exception {
        // Given
        String name = "addon.yap";
        String status = "release";
        String version = "1.0.0";
        AddOn addOn = new AddOn(createAddOnFile(name, status, version));
        AddOn newestAddOn = new AddOn(createAddOnFile(name, status, version));
        newestAddOn.getFile().setLastModified(System.currentTimeMillis() + 1000);
        // When
        boolean update = newestAddOn.isUpdateTo(addOn);
        // Then
        assertThat(update, is(equalTo(true)));
    }

    @Test
    void shouldNotBeUpdateIfFileIsOlderWithSameStatusAndVersion() throws Exception {
        // Given
        String name = "addon.yap";
        String status = "release";
        String version = "1.0.0";
        AddOn addOn = new AddOn(createAddOnFile(name, status, version));
        AddOn newestAddOn = new AddOn(createAddOnFile(name, status, version));
        newestAddOn.getFile().setLastModified(System.currentTimeMillis() + 1000);
        // When
        boolean update = addOn.isUpdateTo(newestAddOn);
        // Then
        assertThat(update, is(equalTo(false)));
    }

    @Test
    void shouldBeUpdateIfOtherAddOnDoesNotHaveFileWithSameStatusAndVersion() throws Exception {
        // Given
        String name = "addon.yap";
        String status = "release";
        String version = "1.0.0";
        AddOn addOn = new AddOn(createAddOnFile(name, status, version));
        AddOn addOnWithoutFile = new AddOn(createAddOnFile(name, status, version));
        addOnWithoutFile.setFile(null);
        // When
        boolean update = addOn.isUpdateTo(addOnWithoutFile);
        // Then
        assertThat(update, is(equalTo(true)));
    }

    @Test
    void shouldNotBeUpdateIfCurrentAddOnDoesNotHaveFileWithSameStatusAndVersion() throws Exception {
        // Given
        String name = "addon.yap";
        String status = "release";
        String version = "1.0.0";
        AddOn addOn = new AddOn(createAddOnFile(name, status, version));
        AddOn addOnWithoutFile = new AddOn(createAddOnFile(name, status, version));
        addOnWithoutFile.setFile(null);
        // When
        boolean update = addOnWithoutFile.isUpdateTo(addOn);
        // Then
        assertThat(update, is(equalTo(false)));
    }

    @Test
    void testCanLoadAddOnNotBefore() throws Exception {
        AddOn ao = new AddOn(createAddOnFile("test-alpha-1.yap", "alpha", "1"));
        ao.setNotBeforeVersion("2.4.0");
        assertTrue(ao.canLoadInVersion("2.4.0"));

        ao.setNotBeforeVersion("2.4.0");
        assertTrue(ao.canLoadInVersion("2.4.0"));
        assertTrue(ao.canLoadInVersion("2.5.0"));
        assertFalse(ao.canLoadInVersion("1.4.0"));
        assertFalse(ao.canLoadInVersion("2.0.alpha"));
    }

    @Test
    void testCanLoadAddOnNotFrom() throws Exception {
        AddOn ao = new AddOn(createAddOnFile("test-alpha-1.yap", "alpha", "1"));
        ao.setNotBeforeVersion("2.4.0");
        ao.setNotFromVersion("2.8.0");
        assertTrue(ao.canLoadInVersion("2.4.0"));
        assertTrue(ao.canLoadInVersion("2.5.0"));
        assertTrue(ao.canLoadInVersion("2.7.0"));
        assertFalse(ao.canLoadInVersion("2.8.0"));
        assertFalse(ao.canLoadInVersion("2.8.0.1"));
        assertFalse(ao.canLoadInVersion("2.9.0"));
    }

    @Test
    void testCanLoadAddOnNotBeforeNotFrom() throws Exception {
        AddOn ao = new AddOn(createAddOnFile("test-alpha-1.yap", "alpha", "1"));
        ao.setNotBeforeVersion("2.4.0");
        assertTrue(ao.canLoadInVersion("2.4.0"));
        ao.setNotFromVersion("2.7.0");
        assertTrue(ao.canLoadInVersion("2.4.0"));
        assertTrue(ao.canLoadInVersion("2.5.0"));
        assertTrue(ao.canLoadInVersion("2.6.0"));
        assertFalse(ao.canLoadInVersion("2.7.0"));
        assertFalse(ao.canLoadInVersion("2.7.0.1"));
        assertFalse(ao.canLoadInVersion("2.8.0"));
    }

    @Test
    void shouldNotBeAddOnFileNameIfNull() throws Exception {
        // Given
        String fileName = null;
        // When
        boolean addOnFileName = AddOn.isAddOnFileName(fileName);
        // Then
        assertThat(addOnFileName, is(equalTo(false)));
    }

    @Test
    void shouldNotBeAddOnFileNameIfNotEndsWithYapExtension() throws Exception {
        // Given
        String fileName = "addon.txt";
        // When
        boolean addOnFileName = AddOn.isAddOnFileName(fileName);
        // Then
        assertThat(addOnFileName, is(equalTo(false)));
    }

    @Test
    void shouldBeAddOnFileNameIfEndsWithYapExtension() throws Exception {
        // Given
        String fileName = "addon.yap";
        // When
        boolean addOnFileName = AddOn.isAddOnFileName(fileName);
        // Then
        assertThat(addOnFileName, is(equalTo(true)));
    }

    @Test
    void shouldBeAddOnFileNameEvenIfYapExtensionIsUpperCase() throws Exception {
        // Given
        String fileName = "addon.YAP";
        // When
        boolean addOnFileName = AddOn.isAddOnFileName(fileName);
        // Then
        assertThat(addOnFileName, is(equalTo(true)));
    }

    @Test
    void shouldNotBeAddOnIfPathIsNull() throws Exception {
        // Given
        Path file = null;
        // When
        boolean addOnFile = AddOn.isAddOn(file);
        // Then
        assertThat(addOnFile, is(equalTo(false)));
    }

    @Test
    void shouldNotBeAddOnIfPathIsDirectory() throws Exception {
        // Given
        Path file = Files.createDirectory(newTempDir().resolve("addon.yap"));
        // When
        boolean addOnFile = AddOn.isAddOn(file);
        // Then
        assertThat(addOnFile, is(equalTo(false)));
    }

    @Test
    void shouldNotBeAddOnIfFileNameNotEndsWithYapExtension() throws Exception {
        // Given
        Path file = createAddOnFile("addon.txt", "alpha", "1");
        // When
        boolean addOnFile = AddOn.isAddOn(file);
        // Then
        assertThat(addOnFile, is(equalTo(false)));
    }

    @Test
    void shouldNotBeAddOnIfAddOnDoesNotHaveManifestFile() throws Exception {
        // Given
        Path file = createEmptyAddOnFile("addon.yap");
        // When
        boolean addOnFile = AddOn.isAddOn(file);
        // Then
        assertThat(addOnFile, is(equalTo(false)));
    }

    @Test
    void shouldBeAddOnIfPathEndsWithYapExtension() throws Exception {
        // Given
        Path file = createAddOnFile("addon.yap", "alpha", "1");
        // When
        boolean addOnFile = AddOn.isAddOn(file);
        // Then
        assertThat(addOnFile, is(equalTo(true)));
    }

    @Test
    void shouldBeAddOnEvenIfYapExtensionIsUpperCase() throws Exception {
        // Given
        Path file = createAddOnFile("addon.YAP", "alpha", "1");
        // When
        boolean addOnFile = AddOn.isAddOn(file);
        // Then
        assertThat(addOnFile, is(equalTo(true)));
    }

    @Test
    void shouldNotBeValidAddOnIfPathIsNull() throws Exception {
        // Given
        Path file = null;
        // When
        ValidationResult result = AddOn.isValidAddOn(file);
        // Then
        assertThat(result.getValidity(), is(equalTo(ValidationResult.Validity.INVALID_PATH)));
    }

    @Test
    void shouldNotBeValidAddOnIfPathHasNoFileName() throws Exception {
        // Given
        Path file = Paths.get("/");
        // When
        ValidationResult result = AddOn.isValidAddOn(file);
        // Then
        assertThat(result.getValidity(), is(equalTo(ValidationResult.Validity.INVALID_PATH)));
    }

    @Test
    void shouldNotBeValidAddOnIfFileDoesNotHaveYapExtension() throws Exception {
        // Given
        Path file = Files.createFile(newTempDir().resolve("addon.zip"));
        // When
        ValidationResult result = AddOn.isValidAddOn(file);
        // Then
        assertThat(result.getValidity(), is(equalTo(ValidationResult.Validity.INVALID_FILE_NAME)));
    }

    @Test
    void shouldNotBeValidAddOnIfPathIsDirectory() throws Exception {
        // Given
        Path file = Files.createDirectory(newTempDir().resolve("addon.yap"));
        // When
        ValidationResult result = AddOn.isValidAddOn(file);
        // Then
        assertThat(result.getValidity(), is(equalTo(ValidationResult.Validity.FILE_NOT_READABLE)));
    }

    @Test
    void shouldNotBeValidAddOnIfPathIsNotReadable() throws Exception {
        // Given
        Path file = createAddOnFile("addon.yap", "alpha", "1");
        assumeTrue(
                Files.getFileStore(file).supportsFileAttributeView(PosixFileAttributeView.class),
                "Test requires support for POSIX file attributes.");
        Set<PosixFilePermission> perms =
                Files.readAttributes(file, PosixFileAttributes.class).permissions();
        perms.remove(PosixFilePermission.OWNER_READ);
        Files.setPosixFilePermissions(file, perms);
        // When
        ValidationResult result = AddOn.isValidAddOn(file);
        // Then
        assertThat(result.getValidity(), is(equalTo(ValidationResult.Validity.FILE_NOT_READABLE)));
    }

    @Test
    void shouldNotBeValidAddOnIfNotZipFile() throws Exception {
        // Given
        Path file = Files.createFile(newTempDir().resolve("addon.yap"));
        // When
        ValidationResult result = AddOn.isValidAddOn(file);
        // Then
        assertThat(
                result.getValidity(), is(equalTo(ValidationResult.Validity.UNREADABLE_ZIP_FILE)));
        assertThat(result.getException(), is(notNullValue()));
    }

    @Test
    void shouldNotBeValidAddOnIfItHasNoManifest() throws Exception {
        // Given
        Path file = Files.createFile(newTempDir().resolve("addon.yap"));
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(file.toFile()))) {
            zos.putNextEntry(new ZipEntry("Not a manifest"));
            zos.closeEntry();
        }
        // When
        ValidationResult result = AddOn.isValidAddOn(file);
        // Then
        assertThat(result.getValidity(), is(equalTo(ValidationResult.Validity.MISSING_MANIFEST)));
    }

    @Test
    void shouldNotBeValidAddOnIfManifestIsMalformed() throws Exception {
        // Given
        Path file = Files.createFile(newTempDir().resolve("addon.yap"));
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(file.toFile()))) {
            zos.putNextEntry(new ZipEntry(AddOn.MANIFEST_FILE_NAME));
            zos.closeEntry();
        }
        // When
        ValidationResult result = AddOn.isValidAddOn(file);
        // Then
        assertThat(result.getValidity(), is(equalTo(ValidationResult.Validity.INVALID_MANIFEST)));
        assertThat(result.getException(), is(notNullValue()));
    }

    @Test
    void shouldNotBeValidAddOnIfHasMissingLib() throws Exception {
        // Given
        Path file =
                createAddOnFile(
                        "addon.yap",
                        "release",
                        "1.0.0",
                        manifest ->
                                manifest.append("<libs>")
                                        .append("<lib>missing.jar</lib>")
                                        .append("</libs>"));
        // When
        ValidationResult result = AddOn.isValidAddOn(file);
        // Then
        assertThat(result.getValidity(), is(equalTo(ValidationResult.Validity.INVALID_LIB)));
    }

    @Test
    void shouldNotBeValidAddOnIfHasLibWithMissingName() throws Exception {
        // Given
        Path file =
                createAddOnFile(
                        "addon.yap",
                        "release",
                        "1.0.0",
                        manifest ->
                                manifest.append("<libs>")
                                        .append("<lib>dir/</lib>")
                                        .append("</libs>"));
        // When
        ValidationResult result = AddOn.isValidAddOn(file);
        // Then
        assertThat(result.getValidity(), is(equalTo(ValidationResult.Validity.INVALID_LIB)));
    }

    @Test
    void shouldBeValidAddOnIfValid() throws Exception {
        // Given
        Path file = createAddOnFile("addon.yap", "release", "1.0.0");
        // When
        ValidationResult result = AddOn.isValidAddOn(file);
        // Then
        assertThat(result.getValidity(), is(equalTo(ValidationResult.Validity.VALID)));
        assertThat(result.getManifest(), is(notNullValue()));
    }

    @Test
    void shouldFailToCreateAddOnFromNullFile() {
        // Given
        Path file = null;
        // When
        IOException e = assertThrows(IOException.class, () -> new AddOn(file));
        // Then
        assertThat(e.getMessage(), is(AddOn.ValidationResult.Validity.INVALID_PATH.name()));
    }

    @Test
    void shouldFailToCreateAddOnFromFileWithInvalidFileName() throws Exception {
        // Given
        String invalidFileName = "addon.txt";
        Path file = createAddOnFile(invalidFileName, "alpha", "1");
        // When
        IOException e = assertThrows(IOException.class, () -> new AddOn(file));
        // Then
        assertThat(e.getMessage(), is(AddOn.ValidationResult.Validity.INVALID_FILE_NAME.name()));
    }

    @Test
    @SuppressWarnings("deprecation")
    void shouldCreateAddOnFromFileAndUseManifestData() throws Exception {
        // Given
        Path file = createAddOnFile("addon.yap", "beta", "1.6.7");
        // When
        AddOn addOn = new AddOn(file);
        // Then
        assertThat(addOn.getId(), is(equalTo("addon")));
        assertThat(addOn.getStatus(), is(equalTo(AddOn.Status.beta)));
        assertThat(addOn.getVersion().toString(), is(equalTo("1.6.7")));
        assertThat(addOn.getFileVersion(), is(equalTo(1)));
    }

    @Test
    void shouldCreateAddOnWithDotsInId() throws Exception {
        // Given
        Path file = createAddOnFile("addon.x.yap", "release", "1.0.0");
        // When
        AddOn addOn = new AddOn(file);
        // Then
        assertThat(addOn.getId(), is(equalTo("addon.x")));
        assertThat(addOn.getStatus(), is(equalTo(AddOn.Status.release)));
        assertThat(addOn.getVersion().toString(), is(equalTo("1.0.0")));
    }

    @Test
    void shouldIgnoreStatusInFileNameWhenCreatingAddOnFromFile() throws Exception {
        // Given
        Path file = createAddOnFile("addon-alpha.yap", "release", "1");
        // When
        AddOn addOn = new AddOn(file);
        // Then
        assertThat(addOn.getStatus(), is(equalTo(AddOn.Status.release)));
    }

    @Test
    @SuppressWarnings("deprecation")
    void shouldIgnoreVersionInFileNameWhenCreatingAddOnFromFile() throws Exception {
        // Given
        Path file = createAddOnFile("addon-alpha-2.yap", "alpha", "3.2.10");
        // When
        AddOn addOn = new AddOn(file);
        // Then
        assertThat(addOn.getVersion().toString(), is(equalTo("3.2.10")));
        assertThat(addOn.getFileVersion(), is(equalTo(3)));
    }

    @Test
    void shouldReturnNormalisedFileName() throws Exception {
        // Given
        Path file = createAddOnFile("addon.yap", "alpha", "2.8.1");
        AddOn addOn = new AddOn(file);
        // When
        String normalisedFileName = addOn.getNormalisedFileName();
        // Then
        assertThat(normalisedFileName, is(equalTo("addon-2.8.1.yap")));
    }

    @Test
    void shouldHaveNoReleaseDate() throws Exception {
        // Given
        AddOn addOn = new AddOn(createAddOnFile("addon.yap"));
        // When
        String releaseDate = addOn.getReleaseDate();
        // Then
        assertThat(releaseDate, is(nullValue()));
    }

    @Test
    void shouldHaveCorrectSize() throws Exception {
        // Given
        AddOn addOn = new AddOn(createAddOnFile("addon.yap"));
        // When
        long size = addOn.getSize();
        // Then
        assertThat(size, is(equalTo(189L)));
    }

    @Test
    void shouldHaveEmptyBundleByDefault() throws Exception {
        // Given
        Path file = createAddOnFile("addon.yap", "release", "1.0.0");
        AddOn addOn = new AddOn(file);
        // When
        BundleData bundleData = addOn.getBundleData();
        // Then
        assertThat(bundleData, is(notNullValue()));
        assertThat(bundleData.isEmpty(), is(equalTo(true)));
        assertThat(bundleData.getBaseName(), is(equalTo("")));
        assertThat(bundleData.getPrefix(), is(equalTo("")));
    }

    @Test
    void shouldHaveDeclaredBundle() throws Exception {
        // Given
        Path file =
                createAddOnFile(
                        "addon.yap",
                        "release",
                        "1.0.0",
                        manifest -> {
                            manifest.append("<bundle>")
                                    .append("org.yaproxy.Messages")
                                    .append("</bundle>");
                        });
        AddOn addOn = new AddOn(file);
        // When
        BundleData bundleData = addOn.getBundleData();
        // Then
        assertThat(bundleData, is(notNullValue()));
        assertThat(bundleData.isEmpty(), is(equalTo(false)));
        assertThat(bundleData.getBaseName(), is(equalTo("org.yaproxy.Messages")));
        assertThat(bundleData.getPrefix(), is(equalTo("")));
    }

    @Test
    void shouldHaveDeclaredBundleWithPrefix() throws Exception {
        // Given
        Path file =
                createAddOnFile(
                        "addon.yap",
                        "release",
                        "1.0.0",
                        manifest -> {
                            manifest.append("<bundle prefix=\"msgs\">")
                                    .append("org.yaproxy.Messages")
                                    .append("</bundle>");
                        });
        AddOn addOn = new AddOn(file);
        // When
        BundleData bundleData = addOn.getBundleData();
        // Then
        assertThat(bundleData, is(notNullValue()));
        assertThat(bundleData.isEmpty(), is(equalTo(false)));
        assertThat(bundleData.getBaseName(), is(equalTo("org.yaproxy.Messages")));
        assertThat(bundleData.getPrefix(), is(equalTo("msgs")));
    }

    @Test
    void shouldHaveEmptyHelpSetByDefault() throws Exception {
        // Given
        Path file = createAddOnFile("addon.yap", "release", "1.0.0");
        AddOn addOn = new AddOn(file);
        // When
        HelpSetData helpSetData = addOn.getHelpSetData();
        // Then
        assertThat(helpSetData, is(notNullValue()));
        assertThat(helpSetData.isEmpty(), is(equalTo(true)));
        assertThat(helpSetData.getBaseName(), is(equalTo("")));
        assertThat(helpSetData.getLocaleToken(), is(equalTo("")));
    }

    @Test
    void shouldHaveDeclaredHelpSet() throws Exception {
        // Given
        Path file =
                createAddOnFile(
                        "addon.yap",
                        "release",
                        "1.0.0",
                        manifest -> {
                            manifest.append("<helpset>")
                                    .append("org.yaproxy.help.helpset")
                                    .append("</helpset>");
                        });
        AddOn addOn = new AddOn(file);
        // When
        HelpSetData helpSetData = addOn.getHelpSetData();
        // Then
        assertThat(helpSetData, is(notNullValue()));
        assertThat(helpSetData.isEmpty(), is(equalTo(false)));
        assertThat(helpSetData.getBaseName(), is(equalTo("org.yaproxy.help.helpset")));
        assertThat(helpSetData.getLocaleToken(), is(equalTo("")));
    }

    @Test
    void shouldHaveDeclaredHelpSetWithLocaleToken() throws Exception {
        // Given
        Path file =
                createAddOnFile(
                        "addon.yap",
                        "release",
                        "1.0.0",
                        manifest -> {
                            manifest.append("<helpset localetoken=\"%LC%\">")
                                    .append("org.yaproxy.help%LC%.helpset")
                                    .append("</helpset>");
                        });
        AddOn addOn = new AddOn(file);
        // When
        HelpSetData helpSetData = addOn.getHelpSetData();
        // Then
        assertThat(helpSetData, is(notNullValue()));
        assertThat(helpSetData.isEmpty(), is(equalTo(false)));
        assertThat(helpSetData.getBaseName(), is(equalTo("org.yaproxy.help%LC%.helpset")));
        assertThat(helpSetData.getLocaleToken(), is(equalTo("%LC%")));
    }

    @Test
    void shouldDependOnDependency() throws Exception {
        // Given
        YapXmlConfiguration yapVersionsXml = createYapVersionsXml();
        AddOn addOn = createAddOn("AddOn1", yapVersionsXml);
        AddOn dependency = createAddOn("AddOn3", yapVersionsXml);
        // When
        boolean depends = addOn.dependsOn(dependency);
        // Then
        assertThat(depends, is(equalTo(true)));
    }

    @Test
    void shouldNotDependIfNoDependencies() throws Exception {
        // Given
        AddOn addOn = new AddOn(createAddOnFile("AddOn-release-1.yap", "release", "1"));
        AddOn nonDependency = createAddOn("AddOn3", createYapVersionsXml());
        // When
        boolean depends = addOn.dependsOn(nonDependency);
        // Then
        assertThat(depends, is(equalTo(false)));
    }

    @Test
    void shouldNotDependOnNonDependency() throws Exception {
        // Given
        YapXmlConfiguration yapVersionsXml = createYapVersionsXml();
        AddOn addOn = createAddOn("AddOn9", yapVersionsXml);
        AddOn nonDependency = createAddOn("AddOn3", yapVersionsXml);
        // When
        boolean depends = addOn.dependsOn(nonDependency);
        // Then
        assertThat(depends, is(equalTo(false)));
    }

    @Test
    void shouldNotDirectlyDependOnNonDirectDependency() throws Exception {
        // Given
        YapXmlConfiguration yapVersionsXml = createYapVersionsXml();
        AddOn addOn = createAddOn("AddOn1", yapVersionsXml);
        AddOn nonDirectDependency = createAddOn("AddOn8", yapVersionsXml);
        // When
        boolean depends = addOn.dependsOn(nonDirectDependency);
        // Then
        assertThat(depends, is(equalTo(false)));
    }

    @Test
    void shouldNotDependOnItSelf() throws Exception {
        // Given
        YapXmlConfiguration yapVersionsXml = createYapVersionsXml();
        AddOn addOn = createAddOn("AddOn1", yapVersionsXml);
        AddOn sameAddOn = createAddOn("AddOn1", yapVersionsXml);
        // When
        boolean depends = addOn.dependsOn(sameAddOn);
        // Then
        assertThat(depends, is(equalTo(false)));
    }

    @Test
    void shouldDependOnDependencies() throws Exception {
        // Given
        YapXmlConfiguration yapVersionsXml = createYapVersionsXml();
        AddOn addOn = createAddOn("AddOn1", yapVersionsXml);
        AddOn nonDependency = createAddOn("AddOn9", yapVersionsXml);
        AddOn dependency = createAddOn("AddOn3", yapVersionsXml);
        Collection<AddOn> addOns = Arrays.asList(new AddOn[] {nonDependency, dependency});
        // When
        boolean depends = addOn.dependsOn(addOns);
        // Then
        assertThat(depends, is(equalTo(true)));
    }

    @Test
    void shouldNotDirectlyDependOnNonDirectDependencies() throws Exception {
        // Given
        YapXmlConfiguration yapVersionsXml = createYapVersionsXml();
        AddOn addOn = createAddOn("AddOn1", yapVersionsXml);
        AddOn nonDependency = createAddOn("AddOn9", yapVersionsXml);
        AddOn nonDirectDependency = createAddOn("AddOn8", yapVersionsXml);
        Collection<AddOn> addOns = Arrays.asList(new AddOn[] {nonDependency, nonDirectDependency});
        // When
        boolean depends = addOn.dependsOn(addOns);
        // Then
        assertThat(depends, is(equalTo(false)));
    }

    @Test
    void shouldNotDependOnNonDependencies() throws Exception {
        // Given
        YapXmlConfiguration yapVersionsXml = createYapVersionsXml();
        AddOn addOn = createAddOn("AddOn1", yapVersionsXml);
        AddOn nonDependency1 = createAddOn("AddOn1", yapVersionsXml);
        AddOn nonDependency2 = createAddOn("AddOn9", yapVersionsXml);
        Collection<AddOn> addOns = Arrays.asList(new AddOn[] {nonDependency1, nonDependency2});
        // When
        boolean depends = addOn.dependsOn(addOns);
        // Then
        assertThat(depends, is(equalTo(false)));
    }

    @Test
    void shouldBeUpdateToOlderVersionIfNewer() throws Exception {
        // Given
        AddOn olderAddOn = new AddOn(createAddOnFile("addon-2.4.8.yap", "release", "2.4.8"));
        AddOn newerAddOn = new AddOn(createAddOnFile("addon-3.5.9.yap", "release", "3.5.9"));
        // When
        boolean update = newerAddOn.isUpdateTo(olderAddOn);
        // Then
        assertThat(update, is(equalTo(true)));
    }

    @Test
    void shouldNotBeUpdateToNewerVersionIfOlder() throws Exception {
        // Given
        AddOn olderAddOn = new AddOn(createAddOnFile("addon-2.4.8.yap", "release", "2.4.8"));
        AddOn newerAddOn = new AddOn(createAddOnFile("addon-3.5.9.yap", "release", "3.5.9"));
        // When
        boolean update = olderAddOn.isUpdateTo(newerAddOn);
        // Then
        assertThat(update, is(equalTo(false)));
    }

    @Test
    void shouldBeAbleToRunIfItHasNoMinimumJavaVersion() throws Exception {
        // Given
        String minimumJavaVersion = null;
        String runningJavaVersion = "1.8";
        AddOn addOn =
                new AddOn(
                        createAddOnFile("addon-2.4.8.yap", "release", "2.4.8", minimumJavaVersion));
        // When
        boolean canRun = addOn.canRunInJavaVersion(runningJavaVersion);
        // Then
        assertThat(canRun, is(equalTo(true)));
    }

    @Test
    void shouldBeAbleToRunInJava9MajorIfMinimumJavaVersionIsMet() throws Exception {
        // Given
        String minimumJavaVersion = "1.8";
        String runningJavaVersion = "9";
        AddOn addOn =
                new AddOn(
                        createAddOnFile("addon-2.4.8.yap", "release", "2.4.8", minimumJavaVersion));
        // When
        boolean canRun = addOn.canRunInJavaVersion(runningJavaVersion);
        // Then
        assertThat(canRun, is(equalTo(true)));
    }

    @Test
    void shouldBeAbleToRunInJava9MinorIfMinimumJavaVersionIsMet() throws Exception {
        // Given
        String minimumJavaVersion = "1.8";
        String runningJavaVersion = "9.1.2";
        AddOn addOn =
                new AddOn(
                        createAddOnFile("addon-2.4.8.yap", "release", "2.4.8", minimumJavaVersion));
        // When
        boolean canRun = addOn.canRunInJavaVersion(runningJavaVersion);
        // Then
        assertThat(canRun, is(equalTo(true)));
    }

    @Test
    void shouldNotBeAbleToRunInJava9MajorIfMinimumJavaVersionIsNotMet() throws Exception {
        // Given
        String minimumJavaVersion = "10";
        String runningJavaVersion = "9";
        AddOn addOn =
                new AddOn(
                        createAddOnFile("addon-2.4.8.yap", "release", "2.4.8", minimumJavaVersion));
        // When
        boolean canRun = addOn.canRunInJavaVersion(runningJavaVersion);
        // Then
        assertThat(canRun, is(equalTo(false)));
    }

    @Test
    void shouldNotBeAbleToRunInJava9MinorIfMinimumJavaVersionIsNotMet() throws Exception {
        // Given
        String minimumJavaVersion = "10";
        String runningJavaVersion = "9.1.2";
        AddOn addOn =
                new AddOn(
                        createAddOnFile("addon-2.4.8.yap", "release", "2.4.8", minimumJavaVersion));
        // When
        boolean canRun = addOn.canRunInJavaVersion(runningJavaVersion);
        // Then
        assertThat(canRun, is(equalTo(false)));
    }

    @Test
    void shouldReturnLibsInManifest() throws Exception {
        // Given
        String lib1 = "lib1.jar";
        String lib2 = "dir/lib2.jar";
        Path file = createAddOnWithLibs(lib1, lib2);
        // When
        AddOn addOn = new AddOn(file);
        // Then
        assertThat(addOn.getLibs(), contains(new AddOn.Lib(lib1), new AddOn.Lib(lib2)));
    }

    @Test
    void shouldNotBeRunnableIfLibsAreNotInFileSystem() throws Exception {
        // Given
        AddOn addOn = new AddOn(createAddOnWithLibs("lib1.jar", "lib2.jar"));
        // When
        AddOn.AddOnRunRequirements reqs = addOn.calculateRunRequirements(Collections.emptyList());
        // Then
        assertThat(reqs.isRunnable(), is(equalTo(false)));
        assertThat(reqs.hasMissingLibs(), is(equalTo(true)));
        assertThat(reqs.getAddOnMissingLibs(), is(equalTo(addOn)));
    }

    @Test
    void shouldBeRunnableIfLibsAreInFileSystem() throws Exception {
        // Given
        String lib1 = "lib1.jar";
        String lib2 = "lib2.jar";
        AddOn addOn = new AddOn(createAddOnWithLibs(lib1, lib2));
        Path libsDir = newTempDir("libsDir");
        addOn.getLibs().get(0).setFileSystemUrl(libsDir.resolve(lib1).toUri().toURL());
        addOn.getLibs().get(1).setFileSystemUrl(libsDir.resolve(lib2).toUri().toURL());
        // When
        AddOn.AddOnRunRequirements reqs = addOn.calculateRunRequirements(Collections.emptyList());
        // Then
        assertThat(reqs.isRunnable(), is(equalTo(true)));
        assertThat(reqs.hasMissingLibs(), is(equalTo(false)));
        assertThat(reqs.getAddOnMissingLibs(), is(nullValue()));
    }

    @Test
    void shouldCreateAddOnLibFromRootJarPath() throws Exception {
        // Given
        String jarPath = "lib.jar";
        // When
        AddOn.Lib lib = new AddOn.Lib(jarPath);
        // Then
        assertThat(lib.getJarPath(), is(equalTo(jarPath)));
        assertThat(lib.getName(), is(equalTo(jarPath)));
    }

    @Test
    void shouldCreateAddOnLibFromNonRootJarPath() throws Exception {
        // Given
        String name = "lib.jar";
        String jarPath = "dir/" + name;
        // When
        AddOn.Lib lib = new AddOn.Lib(jarPath);
        // Then
        assertThat(lib.getJarPath(), is(equalTo(jarPath)));
        assertThat(lib.getName(), is(equalTo(name)));
    }

    @Test
    void shouldNotHaveFileSystemUrlInAddOnLibByDefault() throws Exception {
        // Given / When
        AddOn.Lib lib = new AddOn.Lib("lib.jar");
        // Then
        assertThat(lib.getFileSystemUrl(), is(nullValue()));
    }

    @Test
    void shouldSetFileSystemUrlToAddOnLib() throws Exception {
        // Given
        AddOn.Lib lib = new AddOn.Lib("lib.jar");
        URL fsUrl = new URL("file:///some/path");
        // When
        lib.setFileSystemUrl(fsUrl);
        // Then
        assertThat(lib.getFileSystemUrl(), is(equalTo(fsUrl)));
    }

    @Test
    void shouldSetNullFileSystemUrlToAddOnLib() throws Exception {
        // Given
        AddOn.Lib lib = new AddOn.Lib("lib.jar");
        lib.setFileSystemUrl(new URL("file:///some/path"));
        // When
        lib.setFileSystemUrl(null);
        // Then
        assertThat(lib.getFileSystemUrl(), is(nullValue()));
    }

    private static YapXmlConfiguration createYapVersionsXml() throws Exception {
        YapXmlConfiguration yapVersionsXml = new YapXmlConfiguration(YAP_VERSIONS_XML);
        return yapVersionsXml;
    }
}
