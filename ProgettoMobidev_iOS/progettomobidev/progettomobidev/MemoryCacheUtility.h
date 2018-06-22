//
//  NSObject+MemoryCacheUtility.h
//  Travel Notes
//
//  Created by ianfire on 27/10/16.
//  Copyright © 2016 capraraedefrancescosoft. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Element.h"

@interface MemoryCacheUtility : NSObject 
// ritorna l'uri dell'element se e' in cache, null altrimenti
+ (NSString *) getElementContentFromCache : (Element *) element;
+ (NSString *) cacheElementContent : (Element *) element;
+ (NSString *) cacheData: (NSData *) data withExtension:(NSString *) extension;
+(Element *) convertElementContentToBase64:(Element *)element;

@end
