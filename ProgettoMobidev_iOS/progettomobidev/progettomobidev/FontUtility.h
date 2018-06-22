//
//  FontUtility.h
//  Travel Notes
//
//  Created by gianpaolo on 04/11/16.
//  Copyright © 2016 capraraedefrancescosoft. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <Foundation/Foundation.h>

@interface FontUtility : NSObject

typedef enum fontType {TITLE, EXTRA, TEXT} FontType;


+ (FontUtility*) getInstance;
- (UIFont*) getFont: (FontType) type andSize :(NSInteger) size;

@end
