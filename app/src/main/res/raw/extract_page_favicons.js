/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

/*
 * Extracts <link> tags from page. We do little here because we don't want logic in both
 * JS and Java, JS is more error-prone, and we don't have testing infrastructure for JS.
 */
(function () {
    var linkRelBlacklist = {
        'stylesheet': true,
        'search': true,
        'canonical': true,
        'alternate': true,
    };

    var linkTags = document.getElementsByTagName('link');
    var desiredIcons = [];
    for (var i = 0; i < linkTags.length; i++) {
        var link = linkTags[i];
        if (linkRelBlacklist[link.rel]) continue;

        desiredIcons.push({
            'href': link.href,
            'rel': link.rel,
            'type': link.type,
            'mask': link.mask,
            'sizes': link.sizes[0], /* DOMTokenList */
        });
    }

    firefoxTvJsInterface.onExtractPageFavicons(document.location.toString(),
            JSON.stringify(desiredIcons));
})();
