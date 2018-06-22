//
//  Utility.h
//  prova rest
//
//  Created by gianpaolo on 10/10/16.
//  Copyright © 2016 caprara. All rights reserved.
//

#ifndef Utility_h
#define Utility_h

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

@interface Utility : NSObject

-(NSDictionary *) dictionaryWithPropertiesOfElement:(id)obj;
-(NSDictionary *) dictionaryWithPropertiesOfJournal:(id)obj;
+(NSNumber *) getCurrentUserFacebookID;
+ (int) ComputeHash : (NSData *) data;
+ (bool) isNetworkAvailable;
+(NSData *)compressImage: (UIImage *) image;
+(NSData *) loadDataContent: (NSString *) filePath;
+(UIImage *)generateThumbImage : (NSString *)filepath;
@end
#endif /* Utility_h */
