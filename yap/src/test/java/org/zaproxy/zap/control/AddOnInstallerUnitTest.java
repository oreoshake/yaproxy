/*
 * Zed Attack Proxy (YAP) and its related class files.
 *
 * YAP is an HTTP/HTTPS proxy for assessing web application security.
 *
 * Copyright 2020 The YAP Development Team
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
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.parosproxy.paros.Constant;
import org.yaproxy.yap.control.AddOn.BundleData;
import org.yaproxy.yap.utils.I18N;

/** Unit test for {@link AddOnInstaller}. */
class AddOnInstallerUnitTest extends AddOnTestUtils {

    @BeforeEach
    void createYapHome() throws Exception {
        Constant.setYapHome(newTempDir("home").toAbsolutePath().toString());
    }

    @AfterEach
    void afterEach() {
        Constant.messages = null;
    }

    @Test
    void shouldReturnAddOnDataDir() throws Exception {
        // Given
        AddOn addOn = new AddOn(createAddOnFile("addOnId.yap"));
        // When
        Path addOnDataDir = AddOnInstaller.getAddOnDataDir(addOn);
        // Then
        assertThat(
                Paths.get(Constant.getYapHome()).relativize(addOnDataDir),
                is(equalTo(Paths.get("addOnData/addOnId/1.0.0"))));
    }

    @Test
    void shouldReturnAddOnLibsDir() throws Exception {
        // Given
        AddOn addOn = new AddOn(createAddOnFile("addOnId.yap"));
        // When
        Path addOnLibsDir = AddOnInstaller.getAddOnLibsDir(addOn);
        // Then
        assertThat(
                Paths.get(Constant.getYapHome()).relativize(addOnLibsDir),
                is(equalTo(Paths.get("addOnData/addOnId/1.0.0/libs"))));
    }

    @Test
    void shouldDeleteLegacyAddOnLibsDir() throws Exception {
        // Given
        AddOn addOnA = new AddOn(createAddOnFile("addOnA.yap"));
        Path addOnALibsDir = Paths.get(Constant.getYapHome(), "addOnData/addOnA/libs");
        createFile(addOnALibsDir.resolve("a_a.jar"));
        createFile(addOnALibsDir.resolve("a_b.jar"));
        AddOn addOnB = new AddOn(createAddOnFile("addOnB.yap"));
        Path addOnBLibsDir = Paths.get(Constant.getYapHome(), "addOnData/addOnB/libs");
        createFile(addOnBLibsDir.resolve("b_a.jar"));
        createFile(addOnBLibsDir.resolve("b_b.jar"));
        List<AddOn> addOns = List.of(addOnA, addOnB);
        // When
        AddOnInstaller.deleteLegacyAddOnLibsDir(addOns);
        // Then
        assertThat(Files.notExists(addOnALibsDir), is(equalTo(true)));
        assertThat(Files.notExists(addOnBLibsDir), is(equalTo(true)));
    }

    @Test
    void shouldNotInstallAddOnLibsIfNone() throws Exception {
        // Given
        AddOn addOn = new AddOn(createAddOnFile("addon.yap"));
        // When
        boolean successfully = AddOnInstaller.installAddOnLibs(addOn);
        // Then
        assertThat(successfully, is(equalTo(true)));
        assertThat(Files.notExists(AddOnInstaller.getAddOnDataDir(addOn)), is(equalTo(true)));
    }

    @Test
    void shouldInstallAddOnLibs() throws Exception {
        // Given
        AddOn addOn = new AddOn(createAddOnWithLibs("lib1", "lib2"));
        // When
        boolean successfully = AddOnInstaller.installAddOnLibs(addOn);
        // Then
        assertThat(successfully, is(equalTo(true)));
        assertInstalledLibs(addOn, "lib1", "lib2");
    }

    @Test
    void shouldInstallAddOnLibsOverwritingExisting() throws Exception {
        // Given
        AddOn addOn = new AddOn(createAddOnWithLibs("lib1", "lib2"));
        Path lib2 = installLib(addOn, "lib2", "FileContents");
        // When
        boolean successfully = AddOnInstaller.installAddOnLibs(addOn);
        // Then
        assertThat(successfully, is(equalTo(true)));
        assertThat(contents(lib2), is(equalTo(DEFAULT_LIB_CONTENTS)));
    }

    @Test
    void shouldInstallMissingAddOnLibs() throws Exception {
        // Given
        AddOn addOn = new AddOn(createAddOnWithLibs("lib1", "lib2"));
        installLib(addOn, "lib2");
        // When
        boolean successfully = AddOnInstaller.installMissingAddOnLibs(addOn);
        // Then
        assertThat(successfully, is(equalTo(true)));
        assertInstalledLibs(addOn, "lib1", "lib2");
    }

    @Test
    void shouldInstallMissingAddOnLibsNotOverwritingExisting() throws Exception {
        // Given
        AddOn addOn = new AddOn(createAddOnWithLibs("lib1", "lib2"));
        Path lib2 = installLib(addOn, "lib2", "FileContents");
        // When
        boolean successfully = AddOnInstaller.installMissingAddOnLibs(addOn);
        // Then
        assertThat(successfully, is(equalTo(true)));
        assertInstalledLibs(addOn, "lib1", "lib2");
        assertThat(contents(lib2), is(equalTo("FileContents")));
    }

    @Test
    void shouldUninstallAddOnLibsAndRemoveDataDirIfEmpty() throws Exception {
        // Given
        AddOn addOn = new AddOn(createAddOnWithLibs("lib1", "lib2"));
        installLib(addOn, "lib1");
        installLib(addOn, "lib2");
        // When
        boolean successfully = AddOnInstaller.uninstallAddOnLibs(addOn);
        // Then
        assertThat(successfully, is(equalTo(true)));
        assertThat(Files.notExists(AddOnInstaller.getAddOnDataDir(addOn)), is(equalTo(true)));
    }

    @Test
    void shouldUninstallAddOnLibsAndKeepDataDirIfNotEmpty() throws Exception {
        // Given
        AddOn addOn = new AddOn(createAddOnWithLibs("lib1", "lib2"));
        Path customFile = createFile(AddOnInstaller.getAddOnDataDir(addOn).resolve("customFile"));
        // When
        boolean successfully = AddOnInstaller.uninstallAddOnLibs(addOn);
        // Then
        assertThat(successfully, is(equalTo(true)));
        assertThat(Files.notExists(addOnDataLibsDir(addOn)), is(equalTo(true)));
        assertThat(Files.exists(customFile), is(equalTo(true)));
    }

    @Test
    void shouldUninstallAllAddOnLibsEvenIfSomeNotDeclared() throws Exception {
        // Given
        AddOn addOn = new AddOn(createAddOnWithLibs("lib1", "lib2"));
        installLib(addOn, "lib1");
        installLib(addOn, "lib2");
        installLib(addOn, "libNotDeclared");
        // When
        boolean successfully = AddOnInstaller.uninstallAddOnLibs(addOn);
        // Then
        assertThat(successfully, is(equalTo(true)));
        assertThat(Files.notExists(AddOnInstaller.getAddOnDataDir(addOn)), is(equalTo(true)));
    }

    @Test
    void shouldNotUninstallAddOnLibsIfNoneDeclared() throws Exception {
        // Given
        AddOn addOn = new AddOn(createAddOnFile("addon.yap"));
        installLib(addOn, "lib1");
        installLib(addOn, "lib2");
        // When
        boolean successfully = AddOnInstaller.uninstallAddOnLibs(addOn);
        // Then
        assertThat(successfully, is(equalTo(true)));
        assertInstalledLibs(addOn, "lib1", "lib2");
    }

    @Test
    void shouldRemoveAddOnResourceBundleOnSoftUninstall() throws Exception {
        // Given
        Constant.messages = mock(I18N.class);
        var addOn = mock(AddOn.class);
        var bundleData = mock(BundleData.class);
        given(addOn.getBundleData()).willReturn(bundleData);
        var bundlePrefix = "prefix";
        given(bundleData.getPrefix()).willReturn(bundlePrefix);
        var callback = mock(AddOnUninstallationProgressCallback.class);
        // When
        boolean successfully = AddOnInstaller.softUninstall(addOn, callback);
        // Then
        assertThat(successfully, is(equalTo(true)));
        verify(Constant.messages).removeMessageBundle(bundlePrefix);
        verifyNoMoreInteractions(Constant.messages);
    }

    @Test
    void shouldNotRemoveAddOnResourceBundleIfNoneOnSoftUninstall() throws Exception {
        // Given
        Constant.messages = mock(I18N.class);
        var addOn = mock(AddOn.class);
        var bundleData = mock(BundleData.class);
        given(addOn.getBundleData()).willReturn(bundleData);
        given(bundleData.getPrefix()).willReturn("");
        var callback = mock(AddOnUninstallationProgressCallback.class);
        // When
        boolean successfully = AddOnInstaller.softUninstall(addOn, callback);
        // Then
        assertThat(successfully, is(equalTo(true)));
        verifyNoInteractions(Constant.messages);
    }

    private static void assertInstalledLibs(AddOn addOn, String... fileNames) throws IOException {
        Path addOnLibsDir = addOnDataLibsDir(addOn);

        try (Stream<Path> files = Files.list(addOnLibsDir)) {
            assertThat(
                    files.map(Path::getFileName).map(Path::toString).collect(Collectors.toList()),
                    containsInAnyOrder(fileNames));
        }
    }

    private static String contents(Path file) throws IOException {
        return new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
    }
}
