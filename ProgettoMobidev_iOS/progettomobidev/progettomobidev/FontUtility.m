//
//  FontUtility.m
//  Travel Notes
//
//  Created by gianpaolo on 04/11/16.
//  Copyright © 2016 capraraedefrancescosoft. All rights reserved.
//

#import "FontUtility.h"

@implementation FontUtility{
    NSString *titleFont;
    NSString *noteFont;
    NSString *extraFont;
}

+ (FontUtility*)getInstance
{
    static FontUtility *sharedFonts = nil;
    @synchronized(self) {
        if (sharedFonts == nil)
            sharedFonts = [[self alloc] init];
    }
    return sharedFonts;
}

- (FontUtility*)init{
    if (self = [super init]){
        titleFont = @"AccanthisADFStd-Bold";
        noteFont = @"AmmysHandwriting";
        extraFont = @"AccanthisADFStd-Regular";
    }
    return self;
}

- (UIFont*) getFont: (FontType) type andSize:(NSInteger)size{
    UIFont *font;
    switch (type) {
        case TEXT:
            font = [UIFont fontWithName:noteFont size:size];
            break;
        case TITLE:
            font = [UIFont fontWithName:titleFont size:size];
            break;
        case EXTRA:
            font = [UIFont fontWithName:extraFont size:size];
            break;
    }
    
    return font;
}

@end
