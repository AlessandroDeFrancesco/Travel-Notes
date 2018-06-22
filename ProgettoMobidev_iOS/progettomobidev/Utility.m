//
//  Utility.m
//  prova rest
//
//  Created by gianpaolo on 10/10/16.
//  Copyright © 2016 caprara. All rights reserved.
//

#import <objc/runtime.h>
#import <FBSDKCoreKit/FBSDKCoreKit.h>
#import <SystemConfiguration/SCNetworkReachability.h>
#import <AVFoundation/AVFoundation.h>

#import "Utility.h"

@implementation Utility

// trasforma l'element in un dizionario per passarlo poi sotto forma di json al server
-(NSDictionary *) dictionaryWithPropertiesOfElement:(id)obj{
    NSMutableDictionary *dict = [NSMutableDictionary dictionary];
    
    unsigned count;
    objc_property_t *properties = class_copyPropertyList([obj class], &count);
    
    for (int i = 0; i < count; i++) {
        NSString *key = [NSString stringWithUTF8String:property_getName(properties[i])];
        Class classObject = NSClassFromString([key capitalizedString]);
        
        id object = [obj valueForKey:key];
        
        if (classObject) {
            id subObj = [self dictionaryWithPropertiesOfElement:object];
            [dict setObject:subObj forKey:key];
        }
        else if([object isKindOfClass:[NSDate class]]){
            NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
            [dateFormatter setDateFormat:@"yyyy-MM-dd'T'HH:mm:ss"];
            NSString *stringDate = [dateFormatter stringFromDate:object];
            [dict setObject:stringDate forKey:key];
        }
        else
        {
            if([key  isEqual: @"idElement"]){
                if(object) [dict setObject:[NSString stringWithFormat:@"%@",object] forKey:@"id"];
            } else if ([key  isEqualToString: @"type"]){
                NSString *type = (NSString*) object;
                if([type intValue] == 0){
                    if(object) [dict setObject:@"VIDEO" forKey:key];
                } else if ([type intValue] == 1){
                    if(object) [dict setObject:@"IMAGE" forKey:key];
                } else if ([type intValue] == 2){
                    if(object) [dict setObject:@"NOTE" forKey:key];
                }
            } else {
                if(object) [dict setObject:[NSString stringWithFormat:@"%@",object] forKey:key];
            }
        }
    }
    
    free(properties);
    return [NSDictionary dictionaryWithDictionary:dict];
}

// trasforma il journal in un dizionario per passarlo poi sotto forma di json al server
-(NSDictionary *) dictionaryWithPropertiesOfJournal:(id)obj{
    NSMutableDictionary *dict = [NSMutableDictionary dictionary];
    
    unsigned count;
    objc_property_t *properties = class_copyPropertyList([obj class], &count);
    
    for (int i = 0; i < count; i++) {
        NSString *key = [NSString stringWithUTF8String:property_getName(properties[i])];
        Class classObject = NSClassFromString([key capitalizedString]);
        
        id object = [obj valueForKey:key];

        if (classObject) {
            id subObj = [self dictionaryWithPropertiesOfJournal:object];
            [dict setObject:subObj forKey:key];
        }
        else if([object isKindOfClass:[NSArray class]])
        {
            NSMutableArray *subObj = [NSMutableArray array];
            for (id o in object) {
                [subObj addObject:[NSString stringWithFormat:@"%@",o]];
                //[subObj addObject:[self dictionaryWithPropertiesOfJournal:o] ];
            }
            [dict setObject:subObj forKey:key];
        }
        else if([object isKindOfClass:[NSDate class]]){
            NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
            [dateFormatter setDateFormat:@"yyyy-MM-dd'T'HH:mm:ss"];
            NSString *stringDate = [dateFormatter stringFromDate:object];
            [dict setObject:stringDate forKey:key];
        }
        else
        {
            if(object) [dict setObject:[NSString stringWithFormat:@"%@",object] forKey:key];
        }
    }
    
    free(properties);
    return [NSDictionary dictionaryWithDictionary:dict];
}

+ (NSNumber *) getCurrentUserFacebookID{
    NSNumberFormatter * f = [[NSNumberFormatter alloc] init];
    [f setNumberStyle:NSNumberFormatterDecimalStyle];
    NSNumber * fbIDValue = [f numberFromString:[FBSDKProfile currentProfile].userID];
    return fbIDValue;
}

+ (int) ComputeHash : (NSData *) data {
    int p = 16777619;
    int hash = (int)2166136261;
    
    const char* fileBytes = (const char*)[data bytes];
    NSUInteger length = [data length];
    NSUInteger index;
    
    for (index = 0; index<length; index++)
    {
        char aByte = fileBytes[index];
        hash = (hash ^ aByte) * p;
    }
    
    hash += hash << 13;
    hash ^= hash >> 7;
    hash += hash << 3;
    hash ^= hash >> 17;
    hash += hash << 5;
    return hash;
}

+(NSData *)compressImage: (UIImage *) sourceImage{
    float oldHeight = sourceImage.size.height;
    float scaleFactor = 720 / oldHeight;
    
    float newWidth = sourceImage.size.width * scaleFactor;
    float newHeight = oldHeight * scaleFactor;
    
    UIGraphicsBeginImageContext(CGSizeMake(newWidth, newHeight));
    [sourceImage drawInRect:CGRectMake(0, 0, newWidth, newHeight)];
    UIImage *newImage = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    
    NSLog(@"Compressed image %f, %f", newWidth, newHeight);
    return UIImagePNGRepresentation(newImage);
}

// verifica se la connessione è presente
+(bool)isNetworkAvailable
{
    SCNetworkReachabilityFlags flags;
    SCNetworkReachabilityRef address;
    address = SCNetworkReachabilityCreateWithName(NULL, "www.apple.com" );
    Boolean success = SCNetworkReachabilityGetFlags(address, &flags);
    CFRelease(address);
    
    bool canReach = success
    && !(flags & kSCNetworkReachabilityFlagsConnectionRequired)
    && (flags & kSCNetworkReachabilityFlagsReachable);
    
    return canReach;
}

// legge il contenuto del file
+(NSData *) loadDataContent: (NSString *) filePath{
    NSError *error = nil;
    NSData *data = [[NSData alloc]initWithContentsOfFile:filePath options:0 error:&error];
    
    if (error) {
        NSLog(@"Error reading data: %@.", [error localizedDescription]);
    }
    
    return data;
}

+(UIImage *)generateThumbImage : (NSString *)filepath
{
    NSURL *url = [NSURL fileURLWithPath:filepath];
    AVAsset *asset = [AVAsset assetWithURL:url];
    AVAssetImageGenerator *imageGenerator = [[AVAssetImageGenerator alloc]initWithAsset:asset];
    imageGenerator.appliesPreferredTrackTransform = YES;
    CMTime time = [asset duration];
    time.value = 0;
    CGImageRef imageRef = [imageGenerator copyCGImageAtTime:time actualTime:NULL error:NULL];
    UIImage *thumbnail = [UIImage imageWithCGImage:imageRef];
    CGImageRelease(imageRef);  // CGImageRef won't be released by ARC
    
    return thumbnail;
}

@end
