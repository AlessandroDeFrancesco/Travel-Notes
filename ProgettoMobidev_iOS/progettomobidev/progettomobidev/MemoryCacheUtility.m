//
//  NSObject+MemoryCacheUtility.m
//  Travel Notes
//
//  Created by ianfire on 27/10/16.
//  Copyright © 2016 capraraedefrancescosoft. All rights reserved.
//

#import "MemoryCacheUtility.h"
#import <CommonCrypto/CommonDigest.h>
#import "Utility.h"

@implementation MemoryCacheUtility

// ritorna l'url dell'element se e' in cache, null altrimenti
+ (NSString *) getElementContentFromCache : (Element *) element {
    NSString *cacheFolder = [self getCacheFolder];
    NSString *ext = @"";
    
    switch(element.type){
        case IMAGE:
            ext = @".png";
            break;
        case VIDEO:
            ext = @".mp4";
            break;
        case NOTE:
            return element.content;
    }
    
    // controllo che ci sia in cache
    NSString *fileName = [[NSString stringWithFormat:@"%i", element.idElement] stringByAppendingString : ext];
    NSString* cacheFile = [cacheFolder stringByAppendingPathComponent: fileName];
    BOOL fileExists = [[NSFileManager defaultManager] fileExistsAtPath:cacheFile];
    
    if(fileExists)
        return cacheFile;
    else
        return nil;
}

// ritorna la cartella per la cache: documents/TravelNotesCache
+ (NSString *) getCacheFolder{
    NSError *error;
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *documentsDirectory = [paths objectAtIndex:0];
    NSString *cacheFolder = [documentsDirectory stringByAppendingPathComponent:@"/TravelNotesCache"];
    
    if (![[NSFileManager defaultManager] fileExistsAtPath:cacheFolder])
        [[NSFileManager defaultManager] createDirectoryAtPath:cacheFolder withIntermediateDirectories:NO attributes:nil error:&error];
    
    return cacheFolder;
}

+ (NSString *) cacheElementContent: (Element *) element {
    NSString *content = element.content;
    
    switch (element.type) {
        case IMAGE:{
            content = [self cacheElement:element withExtension:@"png"];
            break;
        }case VIDEO:{
            content = [self cacheElement:element withExtension:@"mp4"];
            break;
        }case NOTE:
            return element.content;
    }
    
    return content;
}

// salva l'elemento in cache e ritorna l'url
+ (NSString *) cacheElement: (Element *) element withExtension:(NSString *) extension{
    NSString *fileName = [NSString stringWithFormat: @"%i.%@", element.idElement, extension];
    
    // salva l'immagine/video nella cartella di cache
    NSError *error = nil;
    NSString *cacheFolder = [self getCacheFolder];
    NSString *filePath = [cacheFolder stringByAppendingPathComponent : fileName];

    NSData *decodedData = [[NSData alloc] initWithBase64EncodedString:element.content options:NSDataBase64DecodingIgnoreUnknownCharacters];
    [decodedData writeToFile:filePath
                 options:0
                 error:&error];
    
    if (error) {
        NSLog(@"Error in chaching %@.......", [error localizedDescription]);
    } else {
        NSLog(@"%@ cached successfully", filePath);
    }
    
    return filePath;
}

// salva l'immagine/video in cache e ritorna l'url
+ (NSString *) cacheData: (NSData *) data withExtension:(NSString *) extension{
    NSString *hash = [@([Utility ComputeHash:data]) stringValue];
    NSString *fileName = [NSString stringWithFormat: @"%@.%@", hash, extension];
    
    // salva l'immagine/video nella cartella di cache, convertendolo in base64
    NSError *error = nil;
    NSString *cacheFolder = [self getCacheFolder];
    NSString *filePath = [cacheFolder stringByAppendingPathComponent : fileName];
    
    [data writeToFile:filePath
          options:0
          error:&error];
    
    if (error) {
        NSLog(@"Error in chaching %@.......", [error localizedDescription]);
    } else {
        NSLog(@"%@ cached successfully", filePath);
    }
    
    return filePath;
}

+(Element *) convertElementContentToBase64:(Element *)element{
    NSError *error = nil;
    
    NSData *data = [[NSData alloc] initWithContentsOfFile:element.content options:0 error:&error];
    NSString *base64String = [data base64EncodedStringWithOptions:0];
    
    if (error) {
        NSLog(@"Error reading file %@. %@", element.content, [error localizedDescription]);
        assert(error);
    }
    
    element.content = base64String;
    
    return element;
}

@end
